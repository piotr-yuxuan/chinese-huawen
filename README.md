# 華文

* Ideographic Description Sequences
* That repository
 * What is this?
 * What is the meaning of the title?
 * Why that repository?
* LaTeX
 * Why using LaTeX instead of markdown?
 * Preamble
 * Known issues
 * Compilation
* misc

## [Ideographic Description Sequences](https://github.com/kawabata/ids)

Ideographic Description Sequences are meant to break down every ideogram into smaller pieces and describe how those pieces are arranged all together. For example, the two-item character set `{華, 文}` is described by the following sequences:
* `{⿱艹&CDP-8BF8;, ⿱亠, 乂}` where `&CDP-8BF8;` stands for a character still outside of the Unicode ;
* `{⿱艹&CDP-8BF8;, ⿱⿱丶一⿻丿乀}`. `艹` is no more broken down because we stop at radical level.

From that, a wonderful world of big, interesting paths appears. [Emacs and Lisp](https://github.com/kawabata/ids-edit) can be very useful

## That repository

### What is this?

That project was first aimed at finding the most efficient path to learn sinograms. I found that topic interesting and turned my mind to study a bit ideograms set and to contribute to help people in memorizing them.

In a personnal way, a important milestone would be to make consistent enough work to write article.

### What is the meaning of the title?
The first character 華 means something like sinity or chineseness. It's used to refers to Chinese people or Chinese civilisation. 文 is the language. Both together means Chinese language.

Github doesn't accept alphanumerical project name so `chinese-huawen` is just the English translation of 華文.

### Why that repository?

I'm a freedom-oriented person, free both as in free speach and free beer ;-) I believe that feeding an public repository can't hurt me: some wanderer may be willing to help me or to give me some advice.

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

## misc

Chinese language is a fascinating boustrophedon, stoichedon Eastern language. Yes, those two words are uncommon but I learned them some days ago and I think they rock! 😉