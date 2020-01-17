# AmazingEditText

[![](https://jitpack.io/v/ajwad-shaikh/AmazingEditText.svg)
](https://jitpack.io/#ajwad-shaikh/AmazingEditText)

## What is AmazingEditText?

AmazingEditText is a lightweight open-source library that unleashes the
power of the basic Android EditText to offer a stylish ready-to-embed
frontend component. The idea is inspired to replicate EditTexts in story
mode on WhatsApp or Instagram!

## Demo

![Screenshot1](https://i.imgur.com/ryL1EtA.gif)

## Usage

### Gradle

Step 1. Add the JitPack repository to your build file. Add it in your
root `build.gradle` at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the JitPack repository to your build file. Add the
dependency

	dependencies {
	        implementation 'com.github.ajwad-shaikh:AmazingEditText:1.0'
	}

### Maven

Step 1. Add the JitPack repository to your build file. Add repository in
your `.pom` file:

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

Step 2. Add the dependency

	<dependency>
	    <groupId>com.github.ajwad-shaikh</groupId>
	    <artifactId>AmazingEditText</artifactId>
	    <version>1.0</version>
	</dependency>

## How to use?

Simply use *AmazingAutofitEditText* instead of the usual *EditText*
component in your xml files.

```
<com.ajwadshaikh.amazingautofitedittext.AmazingAutofitEditText
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="@string/app_name"
    app:maxTextSize="80sp"
    app:minTextSize="16sp" />
```

There are two most essential available properties.

```
    maxTextSize // sets maximum text size
    minTextSize // sets minimum text size
```

Also you can clone this project and compile sample app module to test
the library in action.

## Troubleshooting

Problems? Check the
[Issues](https://github.com/ajwad-shaikh/AmazingEditText/issues) Tab to
find the solution or create an new issue that I will work upon.

## License

The [MIT](LICENSE.md) License (MIT) Copyright © 2020
[Ajwad Shaikh](https://ajwad-shaikh.github.io/)