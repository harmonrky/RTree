# RTree
Basic RTree implementation for storing spacial data

This is a working RTree for mapping Java Point objects to other values. I struggled to find good documentation on RTrees so I used this wikipedia article (https://en.wikipedia.org/wiki/R-tree) and my knowledge of BTrees to create an implementation. RTrees can store different types of spacial data, but for the sake of simplicity this version stores 2D points of integer values. Again, I couldn't find good documentation on the important node splitting algorithm so I had to come up with my own. The algorithm works but there's probably a better implementation out there. The current version lacks a merge function and a search radius function. The former is used to keep the tree efficient after many element removals (its not a necessary function but it definitely helps). The latter function is used to find other points with in a certain radius of a given point. This allows for data requests such as "Find all coffee shops in a 3 mile radius". Implementing this search shouldn't be too difficult in the future. I also need to work on the add() function as it seems to be working slower than I'd like (around Nlog(N) performance). Searching the tree, however, is working as intended with a performance of log(N) (the log base depends on the set page size. Set at 10 nodes per page by default giving log base 10).