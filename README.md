The following is a technical document where it's first introduced the global structure of that repository: what do folders contain, and where can you what you're looking for? Then we step-by-setp explain how to get a working copy of that project on your computer. We'll target a [GNU / Linux](https://www.gnu.org)[ computer](https://www.archlinux.org/) but no big changes may be required if you're using another operating system.

This document shows neither [evolution](documents/Documentation/Work trail.md) of available features and foreseen bugs to be added nor technical details which can be found both in folder [documentation](documents/Documentation).

> You may give give that [website](http://piotr2b.github.io/chinese-huawen/) a glance as for a general, non programmatical introduction to this project.

> Pour voir le dépôt à une date donnée, suivre ce [lien](https://github.com/piotr2b/chinese-huawen/commits/master) ou cliquer sur « commits » à gauche au dessus de la liste des fichiers.

# Structure

This repository has been used in different ways. It initially hosted a [article recension](documents/Articles recension) whose sources can be found in [bibliography](bibliography/). A state of the art was planned to be written but hasn't been born yet. [Documents](documents/) are placed into the eponym folder which also contains the [general documentation](documents/Documentation) of this repository. The folder [gephi](gephi/) contains output pictures and Gephi files and [data](data/) comprehends all what looks like a database. These two latter folders are used by Java projects stored in [java](java/). The folder [java/lib](java/lib/) is for hypothetical libraries which couldn't be handle with maven.

# Getting started

## 0. Software prerequesite

 * `git` You obviously need [*git*](http://git-scm.com/downloads).
 * *Java 8* You need [Java 8](https://en.wikipedia.org/wiki/Java_version_history#Java_SE_8_.28March_18.2C_2014.29) to be available on your computer. There is no need to establish it as default version.
 * *XeTeX* I use the [XeTeX](https://en.wikipedia.org/wiki/XeTeX) compiler to typeset pdf documents. I don't know at all whether it's relevant but try to have a version number equal or greater than `3.14159265-2.6-0.99991 (TeX Live 2014/Arch Linux)`.
 * [*Gephi*](https://gephi.github.io/) is not required but you may like to use it for graph exploration. Be carefull: it doesn't support yet Java 8 so just keep to Java 7 as default version.
 * *Maven* You must ensure [Maven 3](http://maven.apache.org/download.cgi) is available to build java projects.
 * *MariaDB* I use this [database management system](https://mariadb.com/). It's a GPL-licenced drop-in replacement for MySQL so it can be installed very easily.

## 1. Clone that repository
Just execute the following command line.
```
git clone https://github.com/piotr2b/chinese-huawen chinese-huawen
```
## 2. Retrieve updates

## 3. Propose upgrades
Pull request

## 4. Construct the database

