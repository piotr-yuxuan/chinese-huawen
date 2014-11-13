# Work load path

This document is not up to date and will be soon updated. See Introduction for further details.

Instead of nice planning line which has never been followed I prefer to set a journal with two lists. The first one is ordered and new element are added at its end. It shows what I've been working on. The second is unordrerd, it's just a collection of what I could work on.

Items in the second list can seem closed to current possible implementations but merely are technical issues whilst the latter are rather some abstract.

## What's been done

 * Parse data to be processed
 * Export datafiles to Gephi
 * Use Gephi to get pictures
 * Datamodel and its implementation in a database
 * Maven project
 * Connection to the database
 * Tables created
 * Naive entities created
 * Gephi library integrated
 * Use jOOQ, an object-oriented SQL query tool.
 * Automatic entity creation.
 * Interested into Java 8 monads. They seem very useful for such tools like parsers!
 * Draft: PairMap data structure to store a given value with two possible keys, both deductible from the other.
 * Formal use of TypeResolver with a maven dependency.
 * Use structural change in the repository.
  * Readme has been completely changed and turned into a getting starting help.
  * A webpage has been created.
  * General knowledge know is exposed in the project webpage.
 * ENST 'miniproject' report part (technical detail) has been typesetted. Now we just need to fill it in.

## What to do next:

 * Change logic to ensure IDS are correctly parsed even if components are not sorted in a good order. (implique PairMap)
 * Find a better code structure. Command line parameters and *.conf file may tell what kind of output we want: populate the database for real, dictionary human readable serialisation output, gephi-ready files, just pdf with tree.
 * Find nice way to query the database upon IDS
 * Use Gephi to produce svg
 * Avoid writing SQL in Java but find a library to access full relationel model
 * Compare Chise Ids to other.
 * Send mails to know more about Ids files: who have they been written by? is it traditionnal Chinese? is it Japanese decomposition?
 * Add views to allow Gephi to query the database
 * Find frequency lists and normalize them
 * Find a way to retrieve stroke count
 * Write doc, increase comment number
 * How to deal with data replication?
 * Integrate Unihan and populate the database with variants
 * Write JUnit tests
 * Export data into normalized IDS files.
  * So we can choose to unfold IDS.
  * First, one global file
  * Then, allow to specify code point range
  * Allow export in extended IDS with head and tail.
 * Understand monads
 * Use monads for parser
 * Implements PairMap
 * Understand how to idiomatically use ot avoid TypeResolver.

