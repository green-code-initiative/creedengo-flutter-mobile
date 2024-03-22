# ecoCode Flutter for Dart

![ecoCode Logo](docs/resources/5ekko.png)

Mobile applications, especially those running on battery-powered devices, face a unique challenge in managing their environmental impact. The `ecoCode Flutter for Dart` project is our solution for Flutter developers who are conscious of their applications' energy consumption and ecological footprint. Drawing inspiration from [best practices for Android](https://github.com/cnumr/best-practices-mobile#-android-platform), this project aims to extend these principles to the Flutter ecosystem, focusing on Dart programming language.

## Features

`ecoCode Flutter for Dart` provides a suite of static code analyzers designed to identify and highlight code patterns that could lead to excessive energy consumption, "fatware", or potentially shorten the lifespan of devices. By integrating this tool into your Flutter project, you can ensure your app is as efficient and eco-friendly as possible.

## Best Practices Catalog

The project is built around a comprehensive catalog of eco-friendly best practices tailored for Flutter development, including but not limited to:

- **Optimized API Usage**: Encouraging the use of efficient APIs for tasks like location tracking, networking, and sensor management.
- **Resource Management**: Highlighting the importance of proper allocation and deallocation of resources to prevent leaks that could drain device batteries.
- **Eco-Friendly UI/UX Design**: Promoting UI and UX design choices that reduce energy consumption, such as dark mode and simplified animations.
- **Network Efficiency**: Offering guidelines for making network communications more efficient and reducing the carbon footprint associated with data transfer.
- **Battery Usage Optimization**: Providing strategies to minimize battery usage without compromising user experience.

## Quickstart

### Building the Project

To integrate `ecoCode Flutter for Dart` into your project, start by including it as a dependency in your `pubspec.yaml` file. Then, run the following command to download the package:

```bash
flutter pub get
```

### Running the Analyzer

To analyze your Flutter project with `ecoCode`, run:

```bash
flutter analyze
```

Make sure to configure the `analysis_options.yaml` to include `ecoCode`'s specific rules for a comprehensive analysis.

### Docker Integration

A Dockerfile is included for setting up a SonarQube instance with `ecoCode Flutter for Dart` pre-installed. Use the following command to build and run the Docker container:

```bash
docker-compose up --build -d
```

After initialization, visit [http://localhost:9000](http://localhost:9000) to access the SonarQube dashboard. The default login credentials are `admin`/`admin`.

## Plugin Version Compatibility

| ecoCode Version | SonarQube Version        |
|-----------------|--------------------------|
|        | 9.9 LTS to 10.3          |

## License

This project is licensed under the GPL v3 License - see the [LICENSE](LICENSE) file for details.
