# Slide Show

> La présentation de fin de projet est disponible à l'adresse suivante [`http://piotr2b.github.io/slides/index.html`](http://piotr2b.github.io/slides/index.html). Les fichiers et lex exécutables utilisés pendant la soutenance sont dans le sous-répertoire [éponyme](soutenance/).

# General Background

You may give give that [website](http://piotr2b.github.io) a glance as for a general, non programmatical introduction to this project.

This project is aimed at analysis of the sinogram set with graph and set theories. What’s the best way to learn and remember Chinese characters? Most of Chinese handbooks are built in a Western fashion: daily-life conversation samples bring new vocabulary and syntax examples. This fashion doesn’t take advantage of intrinsic Chinese language structure, which makes easier to discover 貴 when knowing yet 中, 一 and 貝. 貴 stands for expensive or honourable while 貝 refers to shell or money. This semantic link lets it easy to remember both of them.

The first character 華 of the project name means sinity; 文 stands for language. The whole phrase 華文 means Chinese language and is read ㄏㄨㄚˊ ㄨㄣˊ or huáwén.

This site has been in some extent redacted in French because part of this project is being developped whithin the frame of a school project.

# Introduction

The following is a technical document where it's first introduced the global structure of that repository: what do folders contain, and where can you what find you're looking for. Then we step-by-setp explain how to get a working copy of that project on your computer. We'll target a [GNU / Linux](https://www.gnu.org)[ computer](https://www.archlinux.org/) but no big changes may be required if you're using another operating system.

This document shows neither [evolution](documents/Documentation/Work trail.md) of available features and foreseen bugs to be added nor technical details which can be found both in folder [documentation](documents/Documentation).

> Pour voir le dépôt à une date donnée, suivre ce [lien](https://github.com/piotr2b/chinese-huawen/commits/master) ou cliquer sur « commits » à gauche au dessus de la liste des fichiers.

# Structure

This repository has been used in different ways. It initially hosted a [article recension](documents/Articles recension) whose sources can be found in [bibliography](bibliography/). A state of the art was planned to be written but hasn't been born yet. However, you could find general introduction to Chinese language structure on the [project site](http://piotr2b.github.io/chinese-huawen/). [Documents](documents/) are placed into the eponym folder which also contains the [general documentation](documents/Documentation) of this repository. The folder [gephi](gephi/) contains output pictures and Gephi files and [data](data/) comprehends all what looks like a database. These two latter folders are used by Java projects stored in [java](java/). The folder [java/lib](java/lib/) is for hypothetical libraries which couldn't be handle with maven.

# Getting Started

## 0. Software prerequesite
 * `git` You obviously need [*git*](http://git-scm.com/downloads).
 * *Java 8* You need [Java 8](https://en.wikipedia.org/wiki/Java_version_history#Java_SE_8_.28March_18.2C_2014.29) to be available on your computer because of lambda expressions. There is no need to establish it as default version.
 * *XeTeX* I use the [XeTeX](https://en.wikipedia.org/wiki/XeTeX) compiler to typeset pdf documents. I don't know at all whether it's relevant but try to have a version number equal or greater than `3.14159265-2.6-0.99991 (TeX Live 2014/Arch Linux)`.
 * [*Gephi*](https://gephi.github.io/) is not required but you may like to use it for graph exploration. Be carefull: it doesn't support yet Java 8 so just keep to Java 7 as default version.
 * *Maven* You must ensure [Maven 3](http://maven.apache.org/download.cgi) is available to build java projects.
 * *MariaDB* I use this [database management system](https://mariadb.com/). It's a GPL-licenced drop-in replacement for MySQL so it can be installed very easily.

### Installer Maven 3 et Java 8 avec son compte Eole

Il est possible de compiler et de lancer ce projet à partir d'une machine même si maven n'est pas présent dessus et que Java 8 n'y est pas installé. Il faut tout d'abord récuper le JDK 8 qui convient sur [openJDK](https://jdk8.java.net/download.html) et Maven 3 à [cette adresse](http://maven.apache.org/download.cgi). On décompressera ensuite l'archive de maven dans `~/.apache-maven-3.2.3` et celle de Java là : `~/.javahome/jdk1.8.0_25` avant de définir les variables de contextes nécessaires :

```
export M2_HOME=/cal/homes/identifiant/.apache-maven-3.2.3
export M2=$M2_HOME/bin
export JAVA_HOME=/cal/homes/identifiant/.javahome/jdk1.8.0_25
export PATH=$M2:$JAVA_HOME/bin:$PATH
```

On peut vérifier que tout va bien si la commande `mvn -help` ne provoque pas une erreur.

## 1. Get that repository local
Just execute the following command line.
```
git clone https://github.com/piotr2b/chinese-huawen chinese-huawen
```
When you want to retrieve upadtes, it's also a one-line operation to perform in the root folder of your local copy.
```
git pull
```
Oh really you'd like to propose upgrades? That would be awesome! Use GitHub dedicated « Pull Requests » tool and send me a mail.

## 2. Fix `gephi-toolkit` dependency
Just before launching our first build, let's fix a little annoying problem with that dependency. I use a deprecated version (my fault) so you need to put `gephi-toolkit-0.8.7.jar` manually in your repository. Get inspiration from the following command:
```
touch ~/.m2/repository/org/gephi/gephi-toolkit/0.8.7
cp ./chinese-huawen/java/lib/gephi-toolkit-0.8.7.jar ~/.m2/repository/org/gephi/gephi-toolkit/0.8.7
```
## 3. Construct the database and generate entities classes
Go in root folder then execute [`huawen.sql`](data/db/huawen.sql) in your MariaDB client to create a new database names `huawen`.
```
mysql -u root -p < huawen.sql
```
Then create a new user and grant them select and insert rights over `huawen`.
```
create user 'huawen'@'localhost' identified by 'huawen';
grant select, insert on huawen.* to 'huawen'@'localhost';
```
Stay in root directory and unleash jOOQ mighty magic. The first command `mvn compile #` mail fail but don't let it bother you. The second is the most important and should complete accordingly.
```
mvn compile #
mvn exec:java -Dexec.mainClass="org.jooq.util.GenerationTool" -Dexec.args="/huawen.xml"
```
## 4. Enjoy the first build
All should be fine now and you'd just have to rely on your internet connection bandwidth to download maven dependencies in a fast way.
```
mvn clean package
```
This should produce two executable Java packages in your `target` folder amongst which `parser-0.0.1-SNAPSHOT.jar` should embed full dependencies then would be executable everywhere. Notice that although I use Java 8 features, methinks it'd run smooth on a Java 7 virtual machine.
```
java -jar parser-0.0.1-SNAPSHOT.jar -direct ⿱⿱⿱ABC⿱BC -output visual
```
