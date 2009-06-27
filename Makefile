# dla windowsa ";" zamiast ":"
MORELIBS = `find ./bin/WEB-INF/lib/*.jar | tr '\n' :`
CLASSPATH = $(MORELIBS)/usr/share/java/tomcat6-servlet-2.5-api.jar
APPPATH = /srv/tomcat6/webapps/ROOT

SQL_INSTALL=\
	CREATE USER 'disaster_manager'@'localhost' IDENTIFIED BY 'dmpass'; \
	CREATE DATABASE disaster_manager CHARACTER SET 'utf8'; \
	GRANT ALL PRIVILEGES ON disaster_manager.* TO 'disaster_manager'@'localhost'; \
	ALTER DATABASE disaster_manager DEFAULT CHARACTER SET utf8 COLLATE utf8_polish_ci; \
	USE disaster_manager; \
	SET CHARACTER SET utf8; \
	SOURCE db.sql;

# DROP USER może skutkować błędem, więc jest na końcu
SQL_UNINSTALL=\
	DROP DATABASE IF EXISTS disaster_manager; \
	DROP USER 'disaster_manager'@'localhost';

SQL_INSTALL_DEMO=\
	USE disaster_manager; \
	SET CHARACTER SET utf8; \
	SOURCE demo.sql;

all: docs/dokument-wizji.pdf bin

docs/dokument-wizji.pdf: docs/dokument-wizji.tex
	cd docs ;\
	pdflatex dokument-wizji.tex > /dev/null ;\
	pdflatex dokument-wizji.tex > /dev/null

bin:
	mkdir bin
	cp -r web/WEB-INF/ bin/
	find -iregex \./java/.*\.java > bin/srclist
	javac -d bin/WEB-INF/classes/ -classpath $(CLASSPATH) @bin/srclist
	cp -r java/* bin/WEB-INF/classes/
	find -iregex \./bin/.*\.svn.* -delete
	find -iregex \./bin/.*\.java -delete
	rm bin/srclist

install: install-bin install-db install-demo-db hwprobe-perms

hwprobe-perms:
	-sudo chmod 666 /dev/ttyUSB*

install-bin: bin
	sudo mkdir -p $(APPPATH)/WEB-INF
	sudo rm -f -r $(APPPATH)/WEB-INF/*
	sudo cp -f -r ./bin/WEB-INF/* $(APPPATH)/WEB-INF/
	sudo cp -f ./lib/libLinuxSerialParallel._so /lib/libLinuxSerialParallel.so
	sudo cp -f ./lib/libLinuxSerialParallel_g._so /lib/libLinuxSerialParallel_g.so
	sudo cp -f ./lib/javax.comm.properties /usr/share/tomcat6/javax.comm.properties
	sudo /sbin/service tomcat6 restart

uninstall-db:
	sudo /sbin/service mysql start
	@-mysql --user=root -p --force --silent -e "$(SQL_UNINSTALL)"

install-db:
	sudo /sbin/service mysql start
	@mysql --user=root -p -e "$(SQL_INSTALL)"

install-demo-db:
	mysql --user=disaster_manager --password=dmpass -e "$(SQL_INSTALL_DEMO)"

hw-probe/hw-probe.hex:
	cd hw-probe ;\
	make hw-probe.hex

#aby utworzyć paczkę, należy przygotować skompilowany program dm-terminal/dm-terminal.exe
bin-pack: hw-probe/hw-probe.hex docs/dokument-wizji.pdf bin
	mkdir bin-pack
	mkdir bin-pack/docs
	mkdir bin-pack/lib
	cp docs/dokument-wizji.pdf bin-pack/docs/
	cp docs/HW-PROBE bin-pack/docs/
	cp docs/INSTALL bin-pack/docs/
	cp docs/README bin-pack/docs/
	cp -r bin/ bin-pack/bin/
	cp *.sql bin-pack/
	cp Makefile bin-pack/
	cp hw-probe/hw-probe.hex bin-pack/
	cp lib/*.* bin-pack/lib/
	cp dm-terminal/dm-terminal.exe bin-pack/

targz: disaster-manager-bin-rXX.tar.gz
disaster-manager-bin-rXX.tar.gz: bin-pack
	rm -f -r disaster-manager-bin-rXX
	mv bin-pack disaster-manager-bin-rXX
	tar -cf disaster-manager-bin-rXX.tar disaster-manager-bin-rXX/*
	gzip -f disaster-manager-bin-rXX.tar

clean:
	@rm -f docs/dokument-wizji.aux
	@rm -f docs/dokument-wizji.dvi
	@rm -f docs/dokument-wizji.log
	@rm -f docs/dokument-wizji.pdf
	@rm -f docs/dokument-wizji.toc
	@rm -f -r bin
	@rm -f -r bin-pack
	@rm -f -r disaster-manager-bin-r*
	@rm -f dm-terminal/dm-terminal.exe
	@cd hw-probe ;\
	make clean