---
layout: post
title:  "Sinogram Structure"
abstract: "We compare Western and Chinese languages. How do they form new words? Chinese etymology is reachable in most case by just giving a look to the word. This etymology can be described with description sequence. However, some times it's more difficult because a character has a different shape: this is the sinogram variants problem. Finally, we imagine what could be a good Chinese learning order and why it matters."
---	

## Sinogram Structure

##### Etymology

An English word is made with letters, thanks captain Obvious! As seen previously, letters like _e_, _b_ do mean nothing (in main case) by their own. You need to put letters together to get meaning: _be_. You can combine old words from antique languages to make a new one whose meaning can be guessed by its etymology _kilo_ and _metre_ give you kilometre. Sometimes the components are changed: _altogether_ comes from _all_ and an old English noun _tōgædere_, sometimes they are not: _aforesaid_ come from _afore_ and _said_.

Chinese is build the same way with a tiny difference: where Western characters (letters) don't mean anything, Eastern characters (sinogram) are yet [semes](https://en.wikipedia.org/wiki/Seme_%28semantics%29). To make it clear, sinograms are at the same level of Western [etymons](http://en.wiktionary.org/wiki/etymon). Western letters can be compared to Eastern strokes. Thus _a_, _aa_ and _aaa_ can't be understood whilst 木, 林 and 森 mean tree, grove of trees and forest. Chinese etymology is very strong!

##### Description Sequence

The structure of a sinogram gives a lot of information about its etymology. Like English, most of sinograms don't visually change a lot when they're used as etymons (or components): 中, 一 and 貝 automatically gives 貴 which is described by the following [Ideographic Description Sequence (<small>IDS</small>)](https://en.wikipedia.org/wiki/Chinese_character_description_languages#Ideographic_Description_Sequences): ⿳中一貝. The first character, ⿳, is an [Ideographic Description Character (<small>IDC</small>)](http://www.unicode.org/versions/Unicode6.0.0/ch12.pdf). Those characters are used to described sinogram structure. There are twelve ways to compose sinograms so there are accordingly twelve characters: ⿰, ⿱, ⿲, ⿳, ⿴, ⿵, ⿶, ⿷, ⿸, ⿹, ⿺ and ⿻. Such an <small>IDC</small> can be seen as an operator whose arity can be 2 or 3.

Let us see another example. The sinogram 灣 can be described by the following equivalent sequences: 

 * 灣 (yes it's a sequence – yet minimal – for the sake of consistency);
 * ⿰氵彎 (normalized decomposition, there are the fewest characters possible):
 * ⿰氵⿱⿲⿱幺小言⿱幺小弓 (unfold decomposition to radical).

Extended <small>IDS</small> format has also been introduced to build a [structural query system](https://github.com/piotr2b/chinese-huawen/blob/master/bibliography/A%20Structural%20Query%20System%20for%20Han%20Characters.pdf) for sinograms.

However, Unicode hasn't been the first to struggle to encode sinograms. Researchers from Academia Sinica have published an [explanation to handle unencoded sinograms](http://cscl.iis.sinica.edu.tw/cscl/Publication/ICDAT04-WebICS.pdf) by composing them. Their composition system is rather interesting but lacks free and available decomposition data sources. Moreover, I can't figure out how they did encode symbols for their equivalent <small>IDC</small> so I can sadly even not type them.

Those decompositions don't go to stroke scale. For instance, the unfold decomposition of 灣 doen't break 小 down. Such "ending" sinograms are called radicals and are used to sort sinograms à la Western lexicographic order. A de facto standard is the [214 Kangxi radical set](https://en.wikipedia.org/wiki/Kangxi_radical). Other decompositions (such like [Wenlin's Character Description Language](http://www.wenlin.com/cdl/)) go straight to strokes. We'll keep to Unicode <small>IDS</small>.

##### Sinogram Variants

Besides that smooth way to compose Chinese characters, some change quite a lot. 部 is visually made of 咅 and ⻏ but the latter sinogram ⻏ is a variant of 邑. To make things more awkward, ⻏ refers to 邑 when put on the right side of the compound sinogram but refers to 阜 on its left. Different versions of a same abstract character are called variants. Variants also depends on the exogen factors just like English language sometimes differs depending on speaker culture or citizenship. I've chosen in previous examples to talk about _kilometre_ but American English spells it _kilometer_. In the same process, sinogram glyphs can be slightly different for Japanese, Korean or Chinese languages. it's even the mess in Chinese language itself where can be found simplified and traditionnal ways to write one sinogram.

There is no one-to-one link with variants, even not one-to-many. So variation patterns can be rather complex and involve many-to-many links. Variants are properly defined in a [report by the Unicode consortium](http://www.unicode.org/reports/tr38/#N10211).

##### Learning Order

We could early guess at this point that it may be interesting to learn etymons before compound sinograms to make the latter both easier to understand and remember. This process can be compared with Spanish. You can learn it only and no other languages else but it's so much easier to learn it when you already know Latin! Moreover, you need to know Latin to understand deeply that language. Anyway, would you learn both Latin and Spanish before going to Spain in vacations? Sure not, because it implies a bigger workload. This is different from Chinese because the Chinese language is more than three-thousand-year old thus uses its own old words as etymons and doesn't need older previous languages – actually Chinese is the main first Asian written language. To put it in a nutshell, learning order counts for Chinese far more than for Western languages.
