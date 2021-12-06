# Presentation

This project propose a simple model of a command tree. Each children of a tree correspond to an argument for a specific command.

# Download

First you need to download this project on your computer. To do so, you can use the following command line :

```git
git clone https://github.com/Pierre-Emmanuel41/command-tree.git --recursive
```

and then double click on the deploy.bat file. This will deploy this project and all its dependencies on your computer. Which means it generates the folder associated to this project and its dependencies in your .m2 folder. Once this has been done, you can add the project as maven dependency on your maven project :

```xml
<dependency>
	<groupId>fr.pederobien</groupId>
	<artifactId>command-tree</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```

To see how you can use thoses features, please have a look to [This tutorial](https://github.com/Pierre-Emmanuel41/command-tree/blob/master/Tutorial.md)