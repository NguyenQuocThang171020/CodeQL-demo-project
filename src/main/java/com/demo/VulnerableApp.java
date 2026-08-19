package com.demo;

import java.io.*;
import java.sql.*;
import java.security.MessageDigest;
import java.util.*;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;

public class VulnerableApp extends HttpServlet {
    public static final String DB_PASSWORD = "SuperSecret123!";
    public static final String API_KEY = "PLACEHOLDER-FAKE-API-KEY-FOR-CODEQL-DEMO";
    private static final String JWT_SECRET = "my-super-secret-jwt-key-2024";

    //  Dead code — CodeQL: java/evaluation-to-constant
    public void deadCode() {
        int x = 5;
        if (false) {
            System.out.println("Never reached: " + x);
        }
        if (1 == 2) {
            System.out.println("cleanup");
        }
    }

    // Resource leak — CodeQL: java/input-resource-leak
    public void resourceLeakFile(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        byte[] buffer = new byte[1024];
        fis.read(buffer);
    }

    // Null dereference — CodeQL: java/dereferenced-value-may-be-null
    public int nullDereference(Map<String, String> data) {
        String value = data.get("key");
        if (value == null) {
            System.out.println("Key not found, nhưng vẫn tiếp tục xử lý bên dưới");
        }
        return value.length();
    }

    // Off-by-one — CodeQL: java/index-out-of-bounds
    public void offByOne(int[] arr) {
        for (int i = 0; i <= arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    // Useless condition — CodeQL: java/comparison-of-identical-expressions
    public boolean uselessCondition(int x) {
        if (x == x) {
            return true;
        }
        return false;
    }

    // SQL Injection — CodeQL: java/sql-injection
    public void sqlInjection(HttpServletRequest request) throws Exception {
        String username = request.getParameter("username");
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        Statement stmt = DbUtil.getConnection().createStatement();
        stmt.executeQuery(query);
    }

    // Command Injection — CodeQL: java/command-line-injection
    public void commandInjection(HttpServletRequest request) throws IOException {
        String host = request.getParameter("host");
        Runtime.getRuntime().exec("ping " + host);
    }

    // Stored XSS — CodeQL: java/xss
    public void storedXss(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String comment = request.getParameter("comment");
        CommentStore.save(comment);
        response.getWriter().write("<div>" + comment + "</div>");
    }

    //  Unsafe Deserialization — CodeQL: java/unsafe-deserialization
    public Object unsafeDeserialize(HttpServletRequest request) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(request.getInputStream());
        return ois.readObject();
    }

    // Crypto yếu MD5 — CodeQL: java/weak-cryptographic-algorithm
    public String weakHashMd5(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return new String(md.digest(password.getBytes()));
    }

    // XXE — CodeQL: java/xxe
    public String xxeVulnerable(HttpServletRequest request) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        builder.parse(new InputSource(request.getInputStream()));
        return "parsed";
    }

    // Unused variable/parameter — CodeQL: java/local-variable-is-never-read + java/unused-parameter
    public void unusedStuff(String unusedParam) {
        int unusedLocalVar = 42;
        String result = "done";
        System.out.println(result);
    }

    // Thiếu @Override — CodeQL: java/missing-override-annotation
    public interface Shape {
        double area();
    }

    public static class Circle implements Shape {
        double R;
        public double area() {
            return 3.14 * R * R;
        }
    }

    // Sử dụng API cũ — CodeQL: java/deprecated-call
    public Integer deprecatedApiUsage(String value) {
        return new Integer(value);
    }

    // Open Redirect — CodeQL: java/unvalidated-url-redirection
    public void openRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String target = request.getParameter("next");
        response.sendRedirect(target);
    }

    // Resource leak không try-with-resources — CodeQL: java/output-resource-leak
    public void noTryWithResources(String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.write("data");
    }

    // Boxing/unboxing thừa trong loop — CodeQL: java/inefficient-boxed-constructor
    public long explicitBoxingInLoop(int n) {
        long sum = 0L;
        for (int index = 0; index < n; index++) {
            Integer boxedValue = new Integer(index);
            sum += boxedValue;
        }
        return sum;
    }

    // Unnecessary object creation trong loop — CodeQL: java/inefficient-string-constructor
    public String unnecessaryObjectCreationInLoop(List<String> items) {
        String result = "";
        for (String item : items) {
            String copiedItem = new String(item);
            result = result + copiedItem;
        }
        return result;
    }

    // String concatenation trong vòng lặp
    public String stringConcatenationInLoop(List<String> items) {
        String result = "";
        for (String item : items) {
            result = result + item + ",";
        }
        return result;
    }
}


// ---------------------------------------------------------------------------
// Các lớp hỗ trợ (stub) để file trên biên dịch được
// ---------------------------------------------------------------------------

class DbUtil {
    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost/test", "root", VulnerableApp.DB_PASSWORD);
    }
}

class CommentStore {
    static void save(String comment) {
        System.out.println("Saved comment: " + comment);
    }
}

class UserRepository {
    static void delete(String userId) {
        System.out.println("Deleted user: " + userId);
    }
}

class Base64EncoderUtil {
    static String encode(byte[] data) {
        return java.util.Base64.getEncoder().encodeToString(data);
    }
}

class SimpleDateFormatWrapper {
    void format(int value) {
        System.out.println("Formatted: " + value);
    }
}