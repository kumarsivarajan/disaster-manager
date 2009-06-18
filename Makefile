#dla windowsa ";" zamiast ":"
CLASSPATH = /usr/share/java/tomcat6-servlet-2.5-api.jar:./bin/WEB-INF/lib/freemarker.jar:./bin/WEB-INF/lib/mail.jar
APPPATH = /srv/tomcat6/webapps/ROOT

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
	sudo rm -f -r $(APPPATH)/WEB-INF/*
	sudo cp -f -r ./bin/WEB-INF/* $(APPPATH)/WEB-INF/
	sudo /sbin/service tomcat6 restart

clean:
	@rm -f docs/dokument-wizji.aux
	@rm -f docs/dokument-wizji.dvi
	@rm -f docs/dokument-wizji.log
	@rm -f docs/dokument-wizji.pdf
	@rm -f docs/dokument-wizji.toc
	@rm -f -r bin