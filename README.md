Top Level Class:

The top level class is Main.java

----------------------------------------------------------
Logic for extracting conditions from WHERE clause:

First, we created a HashMap called "tableMapping" that mapped tableName to where the corresponding table appears in the FROM clause (so the first table in the FROM clause mapped to 0, the second mapped to 1, etc.). We then created a LinkedList of ScanOperators called linkedOperators that adhered to the ordering in tableMapping.

Next, we used BuildSelectConditionsVisitor to traverse the WHERE clause. If a relational expression (i.e. an EqualsTo, a GreaterThan, etc.) involved one column and one constant or two columns from the same table, we tagged this expression as a selection condition. If a relational expression involved two columns from different tables, we tagged it as a join condition.

Relational expressions tagged as selection conditions were placed into an ArrayList at position [tableMapping(tableName)], where tableName was the name of the table involved in the selection. Relational expressions tagged as join conditions, on the other hand, were placed into a second ArrayList at position [MAX(tableMapping.get(tableName1),tableMapping.get(tableName2)) - 1]. As an example to illustrate how this works, assume the tables in our FROM clause, in order, are A, B, C, D, and E. Any join involving just A and B would go to position 0 in the join list; any join involving C but not D or E would go to position 1; any join involving D but not E would map to position 2; and finally any join involving E would go to position 3. Note that when expressions were added to an ArrayList, if there was already something there, we And'ed the old expression and new expression.

We then traversed the list of selection conditions. If a selection expression was not null, we replaced the ScanOperator in linkedOperators corresponding to the table specified in the selection condition with an appropriate SelectOperator. This was easy because if a selection expression and scan operator refered to the same table, they had the same position in their respective lists.

Finally, we added on joins. To do this, we traversed the list of join expressions. For each expression, we popped the first two elements off of linkedOperators, made them children of a JoinOperator corresponding to our current join expression, and pushed this JoinOperator to the front of the linkedOperators. Ultimately, we eventually end up with one element in linkedOperators. This operator is the root of a left-deep join tree corresponding to the order specified in the FROM clause.

-----------------------------------------------------------
Location of file separators in case of any problems:

Main.java line 30
Main.java line 36
Main.java line 52
Main.java line 67
