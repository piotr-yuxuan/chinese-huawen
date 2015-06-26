:title: Soutenance de stage de fin d'études
:data-transition-duration: 1700
:css: style.css

You can render this presentation to HTML with the command:

    hovercraft index.rst slides
    
And then view the outdir/index.html file to see how it turned out.

If you are seeing this text, and not reading this as source code, you are
doing it wrong! It's going to be confusing and not very useful.

Use The Source, Luke! But first you probably want to read through the
official documentation at https://hovercraft.readthedocs.org/

-----

Soutenance de mini-projet INF723
================================

* `Démonstration`_
* `Rappel du contexte`_
* `Architecture simplifiée`_
* `Les génériques réifiés…`_
* `Parcours libre`_

.. _Démonstration : #démo
.. _Rappel du contexte : #contexte
.. _Architecture simplifiée : #archi
.. _Les génériques réifiés… : #génériques
.. _Parcours libre` : #parcours

----

:id: démo 

Démonstration
=============

.. code:: sh

  cd soutenance/
  alias java=../java/.jdk1.8.0_25/bin/java
  java -jar parser-0.0.1-SNAPSHOT.jar -direct ⿱⿱⿱ABC⿱BC -output visual
  java -jar parser-0.0.1-SNAPSHOT.jar -files chinois.txt,ABC.txt -output visual
  java -jar parser-0.0.1-SNAPSHOT.jar -files incohérence.txt -output files

* `Répertoire d'exemple`_

.. _Répertoire d'exemple : https://github.com/piotr2b/chinese-huawen/tree/master/soutenance

----

:id: contexte

Rappel du contexte
==================

http://piotr2b.github.io/

----

:id: archi

Architecture simplifiée
=======================

* La classe :code:`Main` analyse avec :code:`JCommanderParser` les options de la ligne de commande.
* Un objet :code:`Parser` agrège un flux des différents objets :code:`RowChise` construits à partir des lignes des fichiers passés en arguments
* Chacune de ses lignes :code:`RowChise` est analysée et le :code:`Node` correspondant est ajouté à la structure :code:`Substrate`.
* Cette structure fournit les données qui seront tranformées en :code:`org.gephi.graph.api.DirectedGraph` par :code:`JPreviewFrame`, l'objet graphique appelé par :code:`outVisual()`, une des fonctions d'export.
* :code:`JPreviewFrame` utilise une fonte particulière pour afficher les caractères qui ne font pas partie des blocs Unicode standards. Un algorithme de disposition de dessin est rapidement exécuté.

----

:id: génériques

Les génériques réifiés et la programmation fonctionnelle…
=========================================================

…un point de compréhension subtil et une immense simplification

.. code:: java

  List<String> arrayList = new ArrayList<String>();
  arrayList.add("Hello World!");
  String quote = stringList.getFirst();

.. code:: java

  int sum = numbers.stream()
	.map(x -> x + 1) // stateless, non-interfering function
	.filter(x -> x > 2) // Intermediate operation
	.reduce(0, Integer::sum); // associative, non-interfering, stateless

* `Comparaison de C# et Java`_

.. _Comparaison de C# et Java : https://en.wikipedia.org/wiki/Comparison_of_C_Sharp_and_Java#Type_erasure_versus_reified_generics

----

:id: parcours

Parcours libre
==============
