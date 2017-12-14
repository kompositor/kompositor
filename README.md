# Kompositor

A project templating/bootstrapping engine

Project goal: ~~закрыть ППО~~ instead of focusing on various project "archetypes" or "categories", like
Maven Archetypes (or Lazybones, or Yeoman, or New Project in IntellijIDEA) do, this application allows you to take
very basic project (e.g. gradle-kotlin) and apply various "layers" on it. Layers would be typically various frameworks with dependencies,
e.g. JUnit for testing or Jackson for JSON or whatever. Applying different layers allows you to get pre-configured project with all dependencies in buildscript
out-of-the-box, more like [Spring Initializr](https://start.spring.io) - however, latter has one big disadvantage. It (surprisingly) works only with Spring. 