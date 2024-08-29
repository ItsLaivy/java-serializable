package codes.laivy.serializable.main;

import codes.laivy.serializable.json.JsonSerializable;
import com.google.gson.JsonObject;

import java.io.InvalidClassException;

public class Main {
    public static void main(String[] args) throws InvalidClassException, InstantiationException {
        Test test = new Test("ata");
        JsonObject serialize = (new JsonSerializable()).serialize(test).getAsJsonObject();
        System.out.println(serialize);
        System.out.println((new JsonSerializable()).deserialize(Test.class, serialize));
    }
}