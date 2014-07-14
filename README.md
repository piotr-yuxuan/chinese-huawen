# 華文

[TOC]

## Why that repository?

This is intended to be a long-time project about Graph theoretic analysis of the ideograms set.

That project was first aimed at finding the most efficient path to learn sinograms. I found that topic interesting and turned my mind to study a bit ideograms set and to contribute to help people in memorizing them.

In a personnal way, a important milestone would be to make consistent enough work to write article.

## What the meaning of the title?
The first character 華 means something like sinity or chineseness. It's used to refers to Chinese people or Chinese civilisation. 文 is the language. Both together means Chinese language.

Github doesn't accept alphanumerical project name so `chinese-huawen` is just the English translation of 華文.

## Why using $$$\LaTeX{}$$$ instead of markdown?

You're right, markdown can be read directly on github with no downloads and is lighter than $$$\LaTeX{}$$$ but the latter is pretty easier than markdown in purpose of typesetting hypertext including links, bibliographies and so on.

Nonetheless I've been trying to automatically generate markdown files with pandoc then to not to upload `pdf` here but just `tex` and `md`. I faced some issues (handling bibliographiy-related and `input` commands) then I gave up. I know you more than me about it, please tell me :-)

## [Ideographic Description Sequences](https://github.com/kawabata/ids)

Ideographic Description Sequences are meant to break down every ideogram into smaller pieces and describe how those pieces are arranged all together. For example, the two-item character set `{華, 文}` is described by the following sequences:
* `{⿱艹&CDP-8BF8;, ⿱亠, 乂}` where `&CDP-8BF8;` stands for a character still outside of the Unicode ;
* `{⿱艹&CDP-8BF8;, ⿱⿱丶一⿻丿乀}`. `艹` is no more broken down because we stop at radical level.

From that, a wonderful world of big, interesting paths appears. [Emacs and Lisp](https://github.com/kawabata/ids-edit) can be very useful

## misc

Chinese language is a fascinating boustrophedon, stoichedon Eastern language. Yes, those two words are uncommon but I learned them some days ago ago and I think they rock! 😉

## Licence

All personnal works of mine in that repository are under CC BY-NC-SA 4.0. Of course, files from other projets are not under that copyright.