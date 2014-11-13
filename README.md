> The following is a technical document. An general introduction to this project can be found on its [website](http://piotr2b.github.io/chinese-huawen/).

# How to use this repository
About the repository structure

# Getting started

## 0. Software prerequesite
maven, java 8
Help to deal with classpath issue.

## 1. Clone that repository

## 2. Retrieve updates

## 3. Propose upgrades

## 4. Construct the database 

# Technical details

## Work path

See `Work path.md` for details

## LaTeX

### Why using LaTeX instead of markdown?

You're right, markdown can be read directly on github with no downloads and is lighter than LaTeX but the latter is pretty easier than markdown in purpose of typesetting hypertext including links, bibliographies and so on.

Nonetheless I've been trying to automatically generate markdown files with pandoc then to not to upload `pdf` here but just `tex` and `md`. I faced some issues (handling bibliographiy-related and `input` commands) then I gave up. I know you more than me about it, please tell me :-)

### Preamble

LaTeX files here use preambles as modules, so rather prefer to load preambles with commands from the package [`import`](http://ctan.mines-albi.fr/macros/latex/contrib/import/import.pdf). On the contrary, it's easier to enclose text with `\input`.

### Known issues

An ideogram in standard text is rendered correctly but when typed in `\foreignlanguage` just produces a `?` character.

### Compilation

I use the XeLaTeX compiler.

The option `recorder` is required by the package [`currfile`](http://www.ctan.org/tex-archive/macros/latex/contrib/currfile). More specifically, the `abspath` option loads the sub-package `currfile-abspath` and requires compiler option `recorder` to be used. Thus, as `currfile` *could be used*, your compilation line should look comething like:

```
xelatex -recorder
```

## Idioms

Chinese language is a fascinating boustrophedon, stoichedon Eastern language. Yes, those two words are uncommon but I learned them some days ago and I think they rock!

Please feel free to point out English mistakes! The most obnoxious-English-written parts have been translated from French, please forgive it 😉

## jOOQ code generation

Refer to the jOOQ official website tutorial for further informations.

Given the modified `pom.xml` and the additionnal file `huawen.xml` (aimed at setting bindings with Java project), you may execute the following lines to update Java entities.

```
mvn compile #
mvn exec:java -Dexec.mainClass="org.jooq.util.GenerationTool" -Dexec.args="/huawen.xml"
```
