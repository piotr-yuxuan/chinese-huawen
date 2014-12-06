# What to do next ?

School project has gone. By the way, professor has agreed that project that has pleased them. What to do next?

To put it in a nutshell:
1. first website
2. code improvement

In my humble opinion, I should first complete the theoretic part. It's fun and it could bring interesting concepts. This should be done in a clean way. I should also finish state of the art and check whether it's still up-to-date.

Then, back to implementation. I feel like I could turn that project into a library. It's too small to become a real software and actually I already use it as a toolbox. So library+database could be ok.

1. To find a good, up-to-date database schema upon what Mr Abdesallem shew me and link it:
 * Avoid to compute everything on each time
 * Easy export to Gephi (with views)
 * Good exercice to handle database. However methinks we don't need persistency because node objects are quite far from the database tables. Database may be slower than a file when being row-by-row queried but dbms may provide bulk tool to go way faster.
2. Manipulate IDS data. We could generate normalized IDS list per Unicode block. So sinograms would be broken down within block extent or we could give maximal or mininal forms (according to what we know).
 * To achieve so we need to distinguish between decompositions. That's to say, Chinese decomposition is not the same as Japanese and so on.
 * At that point we need to integrate Unihan because we need to handle variants.
 * Variant handling would allow us to integrate even more IDS from different datasources.
 * So we also need to arrange export methods to be able to say «export that substrate according to Chinese way with transitive reduction ».
3. Finally, integrate frequencies and return sinogram list.
