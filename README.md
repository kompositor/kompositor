# Kompositor

[![Build Status](https://travis-ci.org/kompositor/kompositor.svg?branch=master)](https://travis-ci.org/kompositor/kompositor)

A project templating/bootstrapping engine

Project goal: instead of focusing on different project "archetypes" or "categories", like
Maven Archetypes (or Lazybones, or Yeoman, or New Project in IntellijIDEA) do, this application allows you to take
very basic project (e.g. gradle-kotlin) and apply some "layers" on it. Layers would be typically various frameworks with dependencies,
e.g. JUnit for testing or Jackson for JSON or whatever. Applying different layers allows you to get pre-configured project with all dependencies in buildscript
out-of-the-box, more like [Spring Initializr](https://start.spring.io) - however, latter has one big disadvantage. It (surprisingly) works only with Spring.

## Example usage

```bash
kompositor create <templateName> with <layer1,layer2,...> called <projectName> [optionalParams]
``` 

Create project called 'MyKotlinApp' based on gradle with junit:

```bash
kompositor create gradle-kotlin with junit called MyKotlinApp
``` 

You can specify output folder with `--outdir`:

```bash
kompositor create gradle-kotlin with junit called MyKotlinApp --outdir="test" # Create in test
``` 

Template variables can be overridden with `-Vkey=value`:

```bash
kompositor create gradle-kotlin with junit called MyKotlinApp -Vkotlin.version=1.1.50
``` 

Repeated and default config can be written in `~/.kompositor.yml`. It will be resolved last.

```yaml
group: top.sandwwraith
version: 0.1-SNAPSHOT
kotlin.version: 1.2.10
author.email: sandwwraith@gmail.com
```

You can specify another config file with higher priority via `-c`.

Available templates and layers can be discovered via `kompositor templates` and `kompositor layers`
, correspondingly. By default, they're located in [kompositor/templates](https://github.com/kompositor/templates) and
[kompositor/layers](https://github.com/kompositor/layers) repositories.

## Installation

You can download latest `.zip` from [releases](https://github.com/kompositor/kompositor/releases/latest) page.
Unpack it and add `bin/kompositor` (`bin/kompositor.bat` for Windows) to your path.