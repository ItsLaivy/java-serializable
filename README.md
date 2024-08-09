# Serialization Library 📚

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![License](https://img.shields.io/github/license/ItsLaivy/java-serializable?style=for-the-badge)

## 🌐 Overview

Welcome to the java-serializable library!
This Java library provides a simple way to easily serialize all the objects from java
(including the ones without the `Serializble` interface implementation) with an extreme performance and customization

## 🚀 Features

- **No Dependency on Empty Constructors**: Unlike other serializers like GSON, this library doesn't require an empty constructor for deserialization. This means you can freely use `final` fields in your classes.
- **Support for `final` Fields**: Deserialization occurs without invoking any constructor, allowing you to maintain immutable classes with `final` fields.
- **No Need for [Serializable](https://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html)**: Serialize and deserialize classes without implementing the `Serializable` interface. Though not recommended, it offers flexibility when needed.
- **Clean Output**: The serialized data (e.g., JSON) is free of class names or unnecessary information, making it perfect for use in REST APIs.
- **Array Serialization**: Easily serialize arrays of objects, regardless of whether they implement the `Serializable` interface.
- **High Performance**: The serialization process is optimized for speed and efficiency.
- **Highly Customizable**: Gain full control over the serialization process with extensive customization options.

## 🛠️ Installation

For now, there's no public artifact at the Maven Central for this.
To use the **Java Serializable** library.
You should install it manually at your project
using [Maven Guide to installing 3rd party JARs](https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html)

## ✨ Usage
### Annotation
The library has an optional annotation called `@KnownAs` that you can add to a field.

Here's an example of how to use it:

```java
import codes.laivy.serializable.annotations.KnownAs;

public final class User {

    @KnownAs(name = "user_name") // Optional annotation
    private final String name;
    
    private final int age;

    // Constructors, getters, and setters
}
```

### Serialization Example
Here's an example of how the serialization works with the `@KnownAs` annotation and without it:

```java
import codes.laivy.serializable.json.JsonSerializable;

public class Main {
    public static void main(String[] args) {
        User user = new User("Alice", 30);
        JsonSerializable serializer = new JsonSerializable();

        String json = serializer.serialize(user);
        System.out.println(json);
        // Output: {"user_name":"Alice","age":30}
    }
}
```

<hr>

## 📜 API Documentation
For detailed API documentation, please refer to the Javadoc.

## 🤝 Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a pull request

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

## Acknowledgements 🙏

- Thanks to all the contributors who have helped improve this project.
- Special thanks to the open-source community for their continuous support and contributions.


---

Feel free to reach out if you have any questions or need further assistance!

Happy coding! 🚀