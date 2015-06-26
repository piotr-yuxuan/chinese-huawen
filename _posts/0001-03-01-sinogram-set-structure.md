---
layout: post
title:  "Sinogram Set Structure"
abstract: "We can put sinograms together and use the composition relation to build several graph structures, comment them and explain how we move from one to another to get an higher abstraction. We discuss the structure of the sinogram set. We further define the sinogram frequency in two ways, geographically and graph-theoretically. We also sketch some ways to draw such graphs."
---

## Sinogram Set Structure

##### Math-free summary

We use the composition relation to build a composition network and we rise an open question about the structure of the sinogram set. We further distinguish between two definitions for sinogram frequency. Finally, the more frequent is a sinogram, the closer it is to the centre of the network.

##### Definition of the Composition Relation

Let's define the set $$\mathbb{S}$$ of existing sinograms. This is a finite but still growing set. It's a subset of the infinite thus bigger universe set $$\mathbb{U}$$ of all possible sinograms.

We've seen that sinograms can be broken down in smaller ones, called components. Those components enclose etymology and convey meaning. Composition relation $$\mathcal{R}$$ is a partial order relation on $$\mathbb{S}$$ whith these main properties:

 * Reflexive: $$x$$ is a component of itself. This is interesting to say a single sinogram can be a valid <small>IDS</small>. Moreover <small>IDC</small> has arities from 2 to 3 so if $$x$$ is not a component of itself we can't represent a radical <small>IDS</small>. This is why we choose to have not a strict partial order relation (when it's irreflexive) ;
 * Antisymmetric: $$x$$ is a component of $$y$$ and $$y$$ a component of $$x$$ if and only if $$x = y$$ (it's possible because it's reflexive); 
 * Transitivity: $$x$$ is a component of $$y$$ and $$y$$ a component of $$z$$ if and only if $$x$$ is a component of $$z$$. If $$x$$ is a component of $$z$$ with no third distinct sinogram $$y$$ between them, we say $$x$$ is a nearest component of $$z$$. Else we call $$x$$ a far component of $$z$$.

We can also find minor properties:

 * Serial: for all $$x$$ we can find one component or more (at least: itself because it's reflexive);
 * Set-like: sinograms have a finite number of stroke so the class of its components is a set;
 * Non-euclidean: because it's antisymmetric.

This relation defines extrema.
An element $$x$$ is maximal if and only if there is no element $$y$$ different from $$x$$ such that $$x$$ is a component of $$y$$.
Similarly, An element $$x$$ is minimal if and only if there is no element $$y$$ different from $$x$$ such that $$y$$ is a component of $$x$$. It's also a radical.
The greatest element is a sinogram $$y$$ such as every sinogram $$x$$ is a component of $$y$$. The greatest component might not exist.

We use this fresh relation to attach two sets to each sinogram $$s \in \mathbb{S}$$. The first set $$\mathbb{T}_s$$ contains all sinograms $$t \in \mathbb{S}$$ such like $$t \mathcal{R} s$$ while the second set $$\mathbb{F}_s$$ contains all sinograms $$t \in \mathbb{S}$$ such like $$s \mathcal{R} t$$. Because $$\mathcal{R}$$ is antisymmetric, the intersection of two sets of a given sinogram contains only the sinogram itself. A sinogram $$s \in \mathbb{S}$$ has no other components than itself if and only if $$\mathbb{T}_s = \mathbb{F}_s = \left\{ s \right\}$$. $$\mathbb{T}_v$$ is called the component set of $$v$$ and $$\mathbb{F}_v$$ its etymon set.

##### Constructing Graphs to Reach a Structure

This part is aiming at acting like a natural bridge from what has been told afore to smotthly lead to a sinogram structure. We define several graph structures, comment them and explain why we move to another. Incidentally, we become a little bit more abstract. On the very beginning we define a basic edgeless graph with one vertex for each existing sinogram. Then, we describe several ways to add arcs to link vertices and finally choose one. We further perform an operation to merge some vertices together. On that new graph, vertices stand no more for a sinogram but rather more for an etymological class of sinograms.

As first step, we consider a graph $$G = \left(V, E\right)$$ whose vertex set $$V$$ is defined such like there is a bijection between $$V$$ and the sinogram set $$\mathbb{S}$$. That bijection allows us to speak about sinogram or vertex as it's the same object. Given a sinogram $$s \in \mathbb{S}$$ and its associated vertex $$v \in V$$, we write $$a \leftrightarrow v$$ to express that relation. The edge set of $$G$$ is currently empty. If we would draw $$G$$, it would merely show lonely dots.

The most basic to populate $$E_G$$ and to link those dots would be to add arcs according to <small>IDS</small>. That means you would draw arcs from a sinogram to its nearest components. In doing so you would get a parsing graph. This is interesting but we can go further. A parsing graph link each sinogram to its components. Etymons are more interesting than components. For a given sinogram couldn't we basically consider the final leaves of its parsing tree? Actually yes we could but transitional sinograms would be skipped. Those latters are etymons too and we want'em all. As we need to go farther than parsing tree, let's build another graph.

As second step, for two vertices $$v, w \in E$$ and two sinograms $$a, b \in \mathbb{S}$$ such like $$v \leftrightarrow a$$ and $$w \leftrightarrow b$$, we draw an arc (that's to say an oriented edge) from $$a$$ to $$b$$ if and only if the two following conditions are satisfied:

* $$a$$ is a component of $$b$$ which means $$a \mathcal{R} b$$;
* there exist no distinct sinograms $$x \in \mathbb{S}$$ such like $$ a \mathcal{R} x$$ and $$x \mathcal{R} b $$.

If $$a = x = b$$, we have $$a \mathcal{R} b$$, $$ a \mathcal{R} c$$ and $$c \mathcal{R} b $$ so it doesn't match then there are no self-loops. Given three distinct sinograms $$a, b, c \in \mathbb{S}$$ such like $$ a \mathcal{R} b$$ and $$b \mathcal{R} c$$ and three vertices $$u$$, $$v$$ and $$w$$ such like $$a \leftrightarrow u$$, $$b \leftrightarrow v$$ and $$c \leftrightarrow w$$ there are an arc from $$u$$ to $$v$$ and another arc from $$v$$ to $$w$$ then $$w$$ is reachable from both $$u$$ and $$v$$ whilst $$v$$ is reachable from $$u$$.

$$G$$ is currently the [transitive reduction](https://en.wikipedia.org/wiki/Transitive_reduction) of the aforementionned parsing graph. It doesn't match perfectly. We draw for instance such an intuitive graph on the left and its transitive reduction on the right. They are equivalent whilst talking about composition and the transitive reduction ensures there is zero or one single path from a sinogram to another. Arcs are top-to-down oriented.

~~~

   a   b    a   b   The right graph is the transitive
    \ /|     \ /    reduction of the left one; they
     c |      c     have the same reachability.
     |/       |     
     d        d     Arcs are ↓.
~~~

To each vertex $$v \in V$$ we associate two subgraphs $$T_v$$ and $$F_v$$ defined as follows: $$F_v$$ is the subgraph of all vertices reachable *from* $$v$$ and $$T_v$$ of all vertices which can lead *to* $$v$$. We give another equivalent definition of $$F_v = V_{\mathbb{F}_v}$$ and $$T_v = V_{\mathbb{T}_v}$$. We also define $$\mathbb{T}_\mathbb{S} = \left\{ T_x, x \in \mathbb{S} \right\}$$ and $$\mathbb{F}_\mathbb{S} = \left\{ F_x, x \in \mathbb{S} \right\}$$. Note that $$F_v$$ is a tree but $$T_v$$ would be a « reversed » tree (arcs are swapped). However, $$T_v$$ can't be seen as the tree of the <small>IDS</small> of $$v$$ because of $$G$$ is a transitive reduction: it's a etymologic tree which contains all possible etymon of $$v$$. We may get interested in [Hasse diagram](https://en.wikipedia.org/wiki/Hasse_diagram). As usual, the size of a tree (or a reversed tree) is defined as the number of vertices it contains including the root and its length as the length of the longest enclosed path.

A vertex out-degree can be up 3 because of <small>IDC</small> arity. Built that way, this graph is a [multitree](https://en.wikipedia.org/wiki/Multitree) which means this is a directed acyclic graph in which the set of nodes reachable from any node form a tree.

~~~

 x       A multitree. Arcs are left-to-right oriented.
  \      
   z     x and y are minimal vertices.
  /      z is a maximal vertex. It's also the greatest.
 y
~~~

$$G$$ is build with the partial order relation $$\mathcal{R}$$ so it's inherited its maxima. Let's call the vertices associated to the maximal sinograms the roots of the multitree and those associated to minimal sinograms the leaves of the multitree.

**Below haven't been upgraded yet to $$H$$. Make it clearer**

As third step we can remark it's possible for two sinograms $$a, b \in \mathbb{S}$$ to share the same etymon tree: $$T_a = T_b$$. To avoid such a duplication, we build a new graph $$H$$ upon $$G$$ by merging two vertices $$u, v \in E_G$$ when $$T_a = T_v$$:

* There is an surjection $$f : \left\{
  \begin{array}{l l}
    V_G \rightarrow V_H\\
    u \mapsto f(i)
  \end{array} \right.$$ such like $$\forall u, v \in V_G, f(u) = f(v)$$ if and only if $$T_u = T_v$$.
* A vertex 

A vertex $$\mathbb{S} \ni a \leftrightarrow v \in V_H$$ from the resulting graph represents the etymology class of all sinograms within $$\mathbb{S}$$ you can get from $$T_a$$.

Be careful: directed tree are usually defined such like the root can reach leaves. Sinogram trees are reversed. I choose to keep going with reversed trees because it makes the composition relation easier to be defined. Moreover, The formal definition of a forest is a multitree with no intertwinned trees. Then it's formally wrong to call such a multitree a forest but it's funnier to perform sylvan path finding ☺

##### Structure of Some Sets

By construction, the sets $$\mathbb{F}_\mathbb{S}$$ and $$\mathbb{T}_\mathbb{S}$$ are subsets of the set of all subsets of $$\mathbb{S}$$. This latter set is called the [power set](https://en.wikipedia.org/wiki/Power_set) and noted $$2^\mathbb{S}$$. We know that $$\left( 2^\mathbb{S}, \cup, \cap, \complement \right)$$ [is a finite Boole algebra](https://en.wikipedia.org/wiki/Algebra_of_sets) then we can build an isomorphism to other Boole algebras. What can we say about $$\mathbb{F}_\mathbb{S}$$ and $$\mathbb{T}_\mathbb{S}$$? This is a thrilling question which can be extended to $$\mathbb{U}$$, knowing non-finite Boole algebra are not isomorphic. If $$\mathbb{F}_\mathbb{S}$$ and $$\mathbb{T}_\mathbb{S}$$ would be some kind of algebra, it would be nice to name it Chinese algebra!

We hereby restrain the scope to finite set $$\mathbb{S}$$ so we get a finite graph and all is finite. This matter for the definition of cartesian product $$\mathbb{T}_a \times \mathbb{T}_b$$ which could be defined as the set of all sinograms which can be composed from each component of $$a$$ and $$b$$. So it would belong to $$\mathbb{U}$$ and not to $$\mathbb{S}$$. Scaling up that sketch to $$\mathbb{U}$$ might get additionnal precision to be added, such like a choice between the transitive reduction and the minimal equivalent graph.

We precise the following sets from component and compound trees. Let's have two sinograms $$a$$ and $$b$$ from $$\mathbb{S}$$.

 * $$ \mathbb{F}_a \times \mathbb{F}_b $$ can be equal to $$ \mathbb{F}_a \cup \mathbb{F}_b$$ because of the structure of $$\left( \mathbb{S}, \mathcal{R} \right)$$ but we can also choose to widen it in $$\mathbb{U}$$. It's a difficult choice to define the right thing;
 * $$ \mathbb{T}_a \times \mathbb{T}_b $$ can be defined as the set of all sinograms which can be composed from each component of $$a$$ and $$b$$. Methinks it's the more natural definition.

We can order those sets (let's check about $$\mathbb{T}$$ and $$\Delta$$):

$$ \mathbb{T}_a \cap \mathbb{T}_b \subset \mathbb{T}_a \cup \mathbb{T}_b \subset \mathbb{T}_a \times \mathbb{T}_b $$

$$ \mathbb{T}_a \setminus \mathbb{T}_b \subset \mathbb{T}_a \Delta \mathbb{T}_b \subset \mathbb{T}_a \times \mathbb{T}_b $$

$$ \mathbb{F}_a \cup \mathbb{F}_b = \mathbb{F}_a \times \mathbb{F}_b \subset \mathbb{T}_a \times \mathbb{T}_b $$

$$ \mathbb{F}_a \cap \mathbb{F}_b \subset \mathbb{F}_a \cup \mathbb{F}_b $$

$$ \mathbb{F}_a \setminus \mathbb{F}_b \subset \mathbb{F}_a \Delta \mathbb{F}_b \subset \mathbb{F}_a \cup \mathbb{F}_b $$


##### Sinogram Frequence

Well… finally what's the most efficient way to learn Chinese? Have we stepped forward or is all this theoretic discussion useless? Let's have a look on such a forest. Arcs are from the top to the bottom, thus arc $$af$$ is $$a \rightarrow f$$.

~~~

  a    b    c    d
  |   /|\   |        A denser forest. Arcs are from the top to the bottom.
   \ / | \  e                        +–––––––––––––––––––––––––––––––––––+
    f  |  \ |               Sinogram | a | b | c | d | e | f | g | h | i |
    \  /   \|                        +–––+–––+–––+–––+–––+–––+–––+–––+–––+
     `g     h              Frequency | 4 | 5 | 4 | 1 | 3 | 3 | 2 | 2 | 1 |
      \    /                         +–––+–––+–––+–––+–––+–––+–––+–––+–––+
       \  /      Component tree size | 1 | 1 | 1 | 1 | 2 | 3 | 4 | 4 | 8 |
        `i                           +–––––––––––––––––––––––––––––––––––+
~~~

Now it's become easier to define the frequence of a sinogram $$\mathbb{S} \ni x \leftrightarrow a \in G$$ as the cardinality of $$F_a$$. Moreover, let's have a look on the underlying non directed graph of the forest in previous illustration. Such graph can be obtained by replacing every arcs (that's to say directed edges) by (non directed) edges.

According to the [usual definition](https://en.wikipedia.org/wiki/Graph_center) of [closeness centrality](https://en.wikipedia.org/wiki/Centrality#Closeness_centrality), the center of this underlying graph is the set of vertices of minimum eccentricity, thus here $$\left\{b, f, h, i\right\}$$. Even if $$f$$, $$h$$ and $$i$$ don't have a big frequency, $$b$$ hereby belongs to the centre and has the biggest frequency. It's clear now that components have a higher frequency than compound so the first assumption may be hereby explained: sorting sinograms by frequency leads to sort out components before compounds.

We define $$f: \mathbb{S} \rightarrow \mathbb{R}^*_+$$ the function which maps a sinogram $$x$$ to its frequency $$f(x)$$. Let's pick up two sinograms $$a$$, $$b$$ from $$\mathbb{S}$$. We have a range of triangle inequalities to be completed.

$$\forall c \in T_a \cup T_b, f\left(c\right) \geq f(u \in F_a) + f(v \in F_b)$$

$$\forall c \in T_a \Delta T_b, ?$$

$$\forall c \in F_a \cup F_b, f\left(c\right) \leq f(u \in T_a) + f(b \in T_b)$$

The following triangle inequalities must be proved right or wrong:

$$\forall c \in T_a \setminus T_b, f\left(c\right) \leq \mathit{abs}(f(a) - f(b))$$

$$\forall c \in T_a \Delta T_b, ?$$

$$\forall c \in F_a \setminus F_b, f\left(c\right) \geq \mathit{abs}(f(u \in F_a) - f(v \in F_b))$$

We may also intuit the higher is a sinogram frequency, the closer its vertex is to the centre so we would just need to find an centrality mesure which is appropriate to directed graph. The [hierarchical closeness](https://en.wikipedia.org/wiki/Hierarchical_closeness) structural centrality measure is an extension from the previous definition. I haven't implemented that measure yet but according to its definition, it may suit our needs.

Actually, let's don't get stuck into technical consideration. Back to real life, frequency doesn't mean hierarchical closeness but merely: how often do I use that word in my speech? Existing datasources provide such statistics. To put it in a nutshell, learning sinograms by frequency means a trade-off between those centrality and speech frequency definitions is mandatory. One a hand, some sinograms are used only as components and never alone; even is they're quite close to the centre, we don't need to learn them because we would never use them. One the other hand, some words are used very often (like figures and numerals) but may be far from the centre.

##### Drawing the Forest

Displaying the messy forest made with thousands of sinograms demands to be performed automatically. Designing an algorithm which can fit is both a very difficult and complex part. Maximal vertices may be clustered to show the forest structure so it may involve [geographical](https://en.wikipedia.org/wiki/R-tree) algorithm. We could also try to layout it with [Force-directed](https://en.wikipedia.org/wiki/Force-directed_graph_drawing) algorithms. The essential points of a such a drawing are to know what we want to be shown and to understand we can't use it to discover better the forest structure: this is up to our imagination.

__The following is still a draft__

