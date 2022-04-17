# Sling specific annotations
### Project information
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/jmigac/sling-specific-annotations/Java%20CI%20with%20Maven?style=for-the-badge)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/jmigac/sling-specific-annotations?style=for-the-badge)
[![GitHub license](https://img.shields.io/github/license/jmigac/sling-specific-annotations?style=for-the-badge)](https://github.com/jmigac/additional-graphql-extensions/blob/main/LICENSE)
![Lines of code](https://img.shields.io/tokei/lines/github/jmigac/sling-specific-annotations?style=for-the-badge)

### Maven dependency intro
Sling specific annotations is a small projects which enables custom annotations for Sling models.
Currently it enables `@LocalDateValueMapValue` and `@LocalDateTimeValueMapValue` specific injection. Mentioned annotation work on the principle by finding Date object from java.util and transforming it into LocalDate or LocalDateTime object as requested per specific annotations.

## How to add following project as dependency

```
<dependency>
    <groupId>com.juricamigac</groupId>
    <artifactId>sling-specific-annotations</artifactId>
    <version>use the latest version</version>
</dependency>
```

Under repositories add the following code block for repository specification
```
<repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/jmigac/sling-specific-annotations</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
    <releases>
        <enabled>true</enabled>
  </releases>
</repository>
```

### Additional dependencies which are required to use the following the extension
```
<dependency>
    <groupId>org.osgi</groupId>
    <artifactId>osgi.annotation</artifactId>
    <version>7.0.0</version>
</dependency>
<dependency>
    <groupId>biz.aQute.bnd</groupId>
    <artifactId>bndlib</artifactId>
    <version>2.4.0</version>
    <scope>provided</scope>
</dependency>
```
### How to use it
As you have injected `Date` object at this time for acquiring Date information in Sling model, with this dependency you can use the following code block to automatically inject `LocalDate` or `LocalDateTime` into Sling models.

```
@LocalDateValueMap
private LocalDate date;
```

```
@LocalDateValueMap(name = "jcr:lastModified", value="node/node2")
private LocalDate date;
```

```
@LocalDateTimeValueMap
private LocalDateTime date;
```

```
@LocalDateTimeValueMap(name = "jcr:lastModified", value="node/node2")
private LocalDatetime date;
```

```
@CurrentPage
private Page page;
```

```
@PageTemplate
private Template template;
```

Annotations `LocalDateValueMapValue` and `LocalDateTimeValueMapValue` have the same properties as ordinary `@ValueMapValue` to inject object with different name or in different structure path.

## Known issues of dependency not getting resolved

If your bundle can't resolve the dependency, there might be a issue that it's not exported to the OSGi, so just import it in your bundle and then expose it to OSGi as shown in the example below.

```
<plugin>
    <groupId>biz.aQute.bnd</groupId>
    <artifactId>bnd-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>bnd-process</id>
            <goals>
                <goal>bnd-process</goal>
            </goals>
            <configuration>
                <bnd><![CDATA[
Import-Package: javax.annotation;version=0.0.0,*
Export-Package: com.juricamigac.slingspecificannotations.*;0.0.2,\
                    ]]></bnd>
            </configuration>
        </execution>
    </executions>
</plugin>
```