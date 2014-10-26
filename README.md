# 華文

* Work load path
* Some concepts
 * Ideographic Description Sequences
 * Current possible implementations
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

# Work load path

* Next I plan to get interested in Gephi visualization
 * Parse data to be processed
 * Export datafiles to Gephi
 * → I'm here: see the pictures/ directory more some graphical result. Please forgive the code mess.
* Handle database
 * Datamodel and its implementation in a database
 * Java bindings
* Query the database
 * How to deal with data duplication -> database normalisation
 * Query database from gephi-toolkit.

# Some concepts

## [Ideographic Description Sequences](https://github.com/kawabata/ids)

Ideographic Description Sequences are defined by the Unicode Consortium. They are meant to break down every ideogram into smaller pieces and describe how those pieces are arranged altogether. The way pieces are compound is defined by Ideographic Description Character:

> ⿰, ⿱, ⿲, ⿳, ⿴, ⿵, ⿶, ⿷, ⿸, ⿹, ⿺, ⿻;

For example, the character 灣 is described by the following equivalent sequences:

`灣 ~ ⿰氵彎 ~ ⿰氵⿱⿲⿱幺小言⿱幺小弓`

## Parsing

What is the most elegant? Do I need to learn [Emacs, Lisp](https://github.com/kawabata/ids-edit) and Perl to efficiently deal with parsing?

## Current possible implementations
(subject to change. It merely skims up the recension)

* How to store ideogram graphs? Graph databases are quite inviting. Such a database may be able to deal with character variants, thanks to the Unicode Consortium  [character-glyph model](https://github.com/piotr2b/chinese-huawen/blob/master/refs/New%20Perspectives%20in%20Sinographic%20Language%20Processing%20through%20the%20Use%20of%20Character%20Structure.pdf) or any other;
* How to query character set? [Query language](https://github.com/piotr2b/chinese-huawen/blob/master/refs/A%20Structural%20Query%20System%20for%20Han%20Characters.pdf) sketch has been drawn to mimic `grep`;
* Signal processing may be useful to break down a character in all possible ways. Either it'd use IDS (so-called [Unicode way](https://en.wikipedia.org/wiki/Chinese_character_description_languages#Ideographic_Description_Sequences)) either it'd choose [Wenlin's CDL way](https://en.wikipedia.org/wiki/Chinese_character_description_languages#CDL). Assuming Unicode way has been chosen, could we break down a character only by  analysing its glyph or would we need to use Wenlin's stroke-end way then to browse for fetched patterns in an already-kown-character database?
* I was first aiming at learning characters better and more easily: given a characters set, what's a best order to learn all of them? This sounds like a sylvan  graph traversal for which we would hop from tree to tree in a forest ^^ Well, actually not: a forest is a disjoint tree union and trees hereby are intertwined. The aforementionned order may be related to [most semantic subcharacter paths](https://github.com/piotr2b/chinese-huawen/blob/master/refs/New%20Perspectives%20in%20Sinographic%20Language%20Processing%20through%20the%20Use%20of%20Character%20Structure.pdf);
* Student could thus build a optimal learning strategy. To sort out by frequency is not [maximal](https://github.com/piotr2b/chinese-huawen/blob/master/refs/Efficient%20learning%20strategy%20of%20Chinese%20characters%20based%20on%20network%20approach.pdf));
* Ideograms can be seen as a set which we could give a [basis](https://en.wikipedia.org/wiki/Basis_%28linear_algebra%29) to. A basis is a free, spanning orthogonal family. It's a radical list. Some attempt have been performed by Ancient lettered Chinese to give radical lists. Can we find criteria to evaluate such list relevance? According to those criteria, can we find the best basis?
* Once we've found such a criteria, we can use it to find the best base we could use to generate characters. We can enhance IDS by adding them some cues about glyph construction and thus we can build a new way for computers to handle Asian scripts. So fonts would be defined as a set of clues. This is similar to Wenlin's Institute way, but something more intersting cause embedded in computer font rendering softwares. So finally they could deduce glyphs of previouly unkown characters.
* Moreover character are compound most of the time. What's the relation between the number *n* of existing characters we can make from *x* characters?
* Several tools can compose new ideograms from existing components. I don't feel enough a valuable typesetter to be useful in any way in that field. Methinks the more than 80,000 ideograms included in unicode make that issue less critical.

## That repository

### What is this?

That project was first aimed at finding the most efficient path to learn sinograms. I found that topic interesting and turned my mind to study a bit ideograms set and to contribute to help people in memorizing them.

### What is the meaning of the title?
The first character 華 means something like sinity or chineseness. It's used to refers to Chinese people or Chinese civilisation. 文 is the language. Both together means Chinese language.

Github merely accepts alphanumerical project name so `chinese-huawen` is just the English translation of 華文.

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

Chinese language is a fascinating boustrophedon, stoichedon Eastern language. Yes, those two words are uncommon but I learned them some days ago and I think they rock!

Please feel free to point out English mistakes! The most obnoxious-English-written parts have been translated from French, please forgive it 😉
