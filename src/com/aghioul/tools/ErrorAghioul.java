package com.aghioul.tools;

import org.bson.Document;

public class ErrorAghioul {
	public static final int JSON_ERROR = 100;
    public static final int SQL_ERROR = 1000;
    public static final int JAVA_ERROR = 10000;
    public static final int SERVICE_ERROR = -1;

    public static Document serviceAccepted(){
        return new Document("status", "Accepted");
    }

    public static Document serviceRefused(String Message, int codeError){
        return new Document("status", "Error").append("message", Message).append("code", codeError);
    }
}
