# Simplifier Session Plugin

## Introduction

The Session-Plugin is an extension to [Simplifier](http://simplifier.io), adding the capability
of storing and managing user session data inside a database.

## Documentation

See here for a short [Plugin documentation PDF](./project/documentation/Session Plugin Doku Stand 1025-11-12.pdf).


## Deployment

You can run the plugin locally. Your Simplifier AppServer has to be running and has to be accessible locally.

### Local Deployment

The build runs the process with SBT locally, Simplifier's plugin registration port and the plugin communication port must be accessible locally.


#### Prerequisites

- A plugin secret has to be available. It can be created in the Plugins section of Simplifier,
  please look [here for an instruction](https://community.simplifier.io/doc/current-release/extend/plugins/plugin-secrets/).
- replace the default secret: <b>XXXXXX</b> in the [PluginRegistrationSecret](./src/main/scala/byDeployment/PluginRegistrationSecret.scala)
  class with the actual secret.
- Simplifier must be running and the <b>plugin.registration</b> in the settings must be configured accordingly.


### Preparation

#### Simplifier Configuration Modification

Copy the file [settings.conf.dist](./src/main/resources/settings.conf.dist) as <b>settings.conf</b> to your installation path and edit the values as needed.
When launching the jar, the config file must be given as a commandline argument.


- Provide the correct ```database_mysql``` data according to your local setup

__Please Note__: Only MySQL is supported!


### Build and Run

At the commandline, run
```bash
sbt compile
```

and then

```bash
sbt run
```