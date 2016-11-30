Top Level Class:

The top level class is Main.java

----------------------------------------------------------
Index Scan High Level Logic:
We use the IndexScanOperator when the plan_builder_config has a flag to use indexes for scans. We check for this condition in the PhysicalPlanBuilder class when our visitor reaches a Select operator. We then check to see if the child is a scan, and if it is we proceed with a visitor IndexExpressionVisitor that determines if the index can be used on the select condition, what the low and high keys are, and whether we can handle the whole selection condition using the index or whether we also need a SelectOperator.


How we set lowkey/highkey & which conditions can be handled via index
These are determined in IndexExpressionVisitor. For a given table S, we provide IndexExpressionVisitor with all of the selection conditions involving S. We then look at all of these expressions individually. 


If we reach an expression that (1) involves a comparison operator compatible with indexes (e.g. equality, greater than, etc), (2) involves the column we have an index on on one side of the comparison, (3) and involves a LongValue on the other side of the expression, then we can use the index to help with this condition. If it is an equality condition, then we set high key and low key to the LongValue involved in the condition. If the condition wants the column to be greater than/greater than or equal to some LongValue, then we update low key. Finally, if the condition wants the column to be less than/less than or equal to some LongValue then we update highkey.


On the other hand, if a selection condition does not meet criteria (1), (2), and (3), then an index cannot be used to help. As such, we add it to a list of conditions the index cannot help with.


Clustered vs. Unclustered
The handling of unclustered and clustered index can be found in the PhysicalPlanBuilder class after we have determined that the query will use an Index Scan operator (specifically this occurs on line 190). We made two classes, UnclusteredIndexScanOperator and ClusteredIndexScanOperator to handle the different types of indexes. To determine which to use, we rely on the information in index_info.txt. This information is then placed into a TableInfo object inside of our DB Catalog. We then query this TableInfo object when we are deciding between UnclusteredIndexScanOperator and ClusteredIndexScanOperator.


Root to Leaf Descent
We repurposed our code from 4320 hw 2 to implement a B+ Tree Search algorithm. 


Search starts by reading the root off of disk into an IndexNode. Inside of each IndexNode, we store a list of keys as well as a list of the page numbers of the IndexNode’s children. We determine which child to go to next based on the list of keys and according to the rules of B+ Tree structure. Once we make this decision, we have this child’s page number, so we read the child from disk. 


We repeat this procedure until we reach a LeafNode, which we then return. Note that this algorithm only instantiates nodes that occur along the path from root to leaf. This is because IndexNodes only store page numbers of their children, not the children themselves.


When an IndexScanOperator wants to use the index, we use our search algorithm to find the leaf node in which lowkey should occur. Once we have this leaf node, we search for the first key >= lowkey. Then we can start retrieving tuples.

