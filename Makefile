#dla windowsa ";" zamiast ":"
CLASSPATH = /usr/share/java/tomcat6-servlet-2.5-api.jar:./bin/WEB-INF/lib/freemarker.jar:./bin/WEB-INF/lib/mail.jar
APPPATH = /srv/tomcat6/webapps/ROOT

SQL_UNINSTALL_1=DROP USER 'disaster_manager'@'localhost';
SQL_UNINSTALL_2=DROP DATABASE disaster_manager;

SQL_INSTALL=\
	CREATE USER 'disaster_manager'@'localhost' IDENTIFIED BY 'dmpass'; \
	CREATE DATABASE disaster_manager CHARACTER SET 'utf8'; \
	GRANT ALL PRIVILEGES ON disaster_manager.* TO 'disaster_manager'@'localhost'; \
	ALTER DATABASE disaster_manager DEFAULT CHARACTER SET utf8 COLLATE utf8_polish_ci; \
	USE disaster_manager; \
	SOURCE db.sql;

all: docs/dokument-wizji.pdf bin

docs/dokument-wizji.pdf: docs/dokument-wizji.tex
	cd docs ;\
	pdflatex dokument-wizji.tex > /dev/null ;\
	pdflatex dokument-wizji.tex > /dev/null

bin: docs/dokument-wizji.pdf
	mkdir bin
	cp -r web/WEB-INF/ bin/
	find -iregex \./java/.*\.java > bin/srclist
	javac -d bin/WEB-INF/classes/ -classpath $(CLASSPATH) @bin/srclist
	cp -r java/* bin/WEB-INF/classes/
	find -iregex \./bin/.*\.svn.* -delete
	find -iregex \./bin/.*\.java -delete
	rm bin/srclist
	
	mkdir bin/docs
	cp docs/dokument-wizji.pdf bin/docs/dokument-wizji.pdf

install: bin
	sudo mkdir -p $(APPPATH)/WEB-INF
	sudo rm -f -r $(APPPATH)/WEB-INF/*
	sudo cp -f -r ./bin/WEB-INF/* $(APPPATH)/WEB-INF/
	sudo /sbin/service tomcat6 restart

uninstall-db:
	sudo /sbin/service mysql start
#	-sudo mysql --force --silent -e "$(SQL_UNINSTALL_1)"
#	-sudo mysql --force --silent -e "$(SQL_UNINSTALL_2)"
	-mysql --user=root -p --force --silent -e "$(SQL_UNINSTALL_1)"
	-mysql --user=root -p --force --silent -e "$(SQL_UNINSTALL_2)"

install-db:
	sudo /sbin/service mysql start
#	sudo mysql -e "$(SQL_INSTALL)"
	mysql -p --user=root -e "$(SQL_INSTALL)"

clean:
	@rm -f docs/dokument-wizji.aux
	@rm -f docs/dokument-wizji.dvi
	@rm -f docs/dokument-wizji.log
	@rm -f docs/dokument-wizji.pdf
	@rm -f docs/dokument-wizji.toc
	@rm -f -r bin
