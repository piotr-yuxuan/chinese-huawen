---
layout: post
title:  "Implementations"
abstract: "This part comes when theoretic frame has been weel-defined. Some ideas directly come from the recension and may be interested to implement because of technical points or to get a deeper comprehension of what we're talking about. Some others may have been not explored yet."
---

## Implementations

Il faut faire gaffe ici de ne pas parler de programmation. On peut toujours renvoyer à des documents du projet.

* Compare Chinese frequency and [that diagram](http://tigger.uic.edu/~ejv/img/thelwallfigure1.jpg). Which viewpoint can we get a similar phenomenom from? Can we give a bounding function?
* Après l'introduction, le parser, bla bla, il faut unifier les variants. Pas tous, mais au moins les variants de l'axe Z (comme 阝). C'est plus simple. Et virer les caractères simplifiés, c'est plus simple.
* How to store ideogram graphs? Graph databases are quite inviting. Such a database may be able to deal with character variants, thanks to the Unicode Consortium  [character-glyph model](https://github.com/piotr2b/chinese-huawen/blob/master/refs/New%20Perspectives%20in%20Sinographic%20Language%20Processing%20through%20the%20Use%20of%20Character%20Structure.pdf) or any other;
* How to query character set? [Query language](https://github.com/piotr2b/chinese-huawen/blob/master/refs/A%20Structural%20Query%20System%20for%20Han%20Characters.pdf) sketch has been drawn to mimic `grep`;
* Signal processing may be useful to break down a character in all possible ways. Either it'd use <small>IDS</small> (so-called [Unicode way](https://en.wikipedia.org/wiki/Chinese_character_description_languages#Ideographic_Description_Sequences)) either it'd choose [Wenlin's <small>CDL</small> way](https://en.wikipedia.org/wiki/Chinese_character_description_languages#CDL). Assuming Unicode way has been chosen, could we break down a character only by  analysing its glyph or would we need to use Wenlin's stroke-end way then to browse for fetched patterns in an already-kown-character database?
* I was first aiming at learning characters better and more easily: given a characters set, what's a best order to learn all of them? This sounds like a sylvan  graph traversal for which we would hop from tree to tree in a forest ^^ Well, actually not: a forest is a disjoint tree union and trees hereby are intertwined. The aforementionned order may be related to [most semantic subcharacter paths](https://github.com/piotr2b/chinese-huawen/blob/master/refs/New%20Perspectives%20in%20Sinographic%20Language%20Processing%20through%20the%20Use%20of%20Character%20Structure.pdf);
* Student could thus build a optimal learning strategy. To sort out by frequency is not [maximal](https://github.com/piotr2b/chinese-huawen/blob/master/refs/Efficient%20learning%20strategy%20of%20Chinese%20characters%20based%20on%20network%20approach.pdf));
* Ideograms can be seen as a set which we could give a [basis](https://en.wikipedia.org/wiki/Basis_%28linear_algebra%29) to. A basis is a free, spanning orthogonal family. It's a radical list. Some attempt have been performed by Ancient lettered Chinese to give radical lists. Can we find criteria to evaluate such list relevance? According to those criteria, can we find the best basis?
* Once we've found such a criteria, we can use it to find the best base we could use to generate characters. We can enhance <small>IDS</small> by adding them some cues about glyph construction and thus we can build a new way for computers to handle Asian scripts. So fonts would be defined as a set of clues. This is similar to Wenlin's Institute way, but something more intersting cause embedded in computer font rendering softwares. So finally they could deduce glyphs of previouly unkown characters. 214 Kangxi radicals is a de facto standard. So it can be improved.
* Moreover character are compound most of the time. What's the relation between the number *n* of existing characters we can make from *x* characters?
* Several tools can compose new ideograms from existing components. I don't feel enough a valuable typesetter to be useful in any way in that field. Methinks the more than 80,000 ideograms included in unicode make that issue less critical.

