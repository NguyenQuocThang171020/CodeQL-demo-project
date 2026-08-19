/**
 * @name String concatenation in loop
 * @description Nối chuỗi bằng + hoặc += bên trong loop — nên dùng StringBuilder.
 * @kind problem
 * @problem.severity recommendation
 * @precision medium
 * @id java/custom-string-concat-in-loop
 * @tags quality performance
 */

import java

from Assignment assign, LoopStmt loop       // Bất kỳ câu lệnh gán nào (=, +=...), Bất kỳ vòng lặp nào (for, while, for-each)
where
  assign.getDest().getType().(RefType).hasQualifiedName("java.lang", "String") and // Chỉ lấy các phép gán mà biến đích có kiểu String
  (
    assign instanceof AssignAddExpr     // Chỉ lấy các phép gán mà biến đích có kiểu String
    or (assign instanceof AssignExpr and assign.getRhs() instanceof AddExpr)        // Trường hợp dùng = kèm phép + ở vế phải
  ) and
  assign.getEnclosingStmt().getEnclosingStmt*() = loop.getBody()  // Kiểm tra câu lệnh gán này nằm bên trong thân vòng lặp
select assign, "Nối chuỗi String bên trong vòng lặp — nên dùng StringBuilder để tránh tạo object thừa."