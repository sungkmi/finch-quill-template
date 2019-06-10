lazy val finchVersion = "0.29.0"
lazy val circeVersion = "0.11.1"
lazy val quillVersion = "3.2.1"
lazy val utestVersion = "0.6.9"
lazy val pgEmbeddedVersion = "0.13.1"

lazy val scribeVersion = "2.7.6"
lazy val acyclicVersion = "0.2.0"
lazy val silencerVersion = "1.4.1"

lazy val sharedSettings = Seq(
  organization := "sungkmi",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.8",

  autoCompilerPlugins := true,
  addCompilerPlugin("com.lihaoyi" %% "acyclic" % acyclicVersion),
  addCompilerPlugin("com.github.ghik" %% "silencer-plugin" % silencerVersion),

  cancelable in Global := true,

  // 2.13 preview
  scalacOptions += "-Xsource:2.13",

  // Linter
  scalacOptions ++= Seq(
    "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
    "-encoding", "utf-8",                // Specify character encoding used by source files.
    "-explaintypes",                     // Explain type errors in more detail.
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
    "-language:higherKinds",             // Allow higher-kinded types
    "-language:implicitConversions",     // Allow definition of implicit functions called views
    "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
    "-P:acyclic:force",                  // Enforce acyclic plugin across all files.
    "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
    "-Xfuture",                          // Turn on future language features.
    "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
    "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
    "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
    "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
    "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
    "-Xlint:option-implicit",            // Option.apply used implicit view.
    "-Xlint:package-object-classes",     // Class or object defined in package object.
    "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
    "-Xlint:unsound-match",              // Pattern match may not be typesafe.
    "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    "-Ypartial-unification",             // Enable partial unification in type constructor inference
    "-Ywarn-dead-code",                  // Warn when dead code is identified.
    "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
    "-Ywarn-numeric-widen",              // Warn when numerics are widened.
    "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals",              // Warn if a local definition is unused.
    "-Ywarn-unused:params",              // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates",            // Warn if a private member is unused.
    "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
  ),

  scalacOptions in (Compile, console) ~= (_.filterNot(Set(
    "-Ywarn-unused:imports",
    "-Xfatal-warnings"
  ))),

  // assembly plugin related
  assemblyMergeStrategy in assembly := {
    case "BUILD" => MergeStrategy.discard
    case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.concat
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  },

  libraryDependencies ++= Seq(
    "com.github.finagle" %% "finchx-core"  % finchVersion,
    "com.github.finagle" %% "finchx-circe"  % finchVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "com.github.ghik" %% "silencer-lib" % silencerVersion % Provided,
    "com.lihaoyi" %% "utest" % utestVersion % Test,
    compilerPlugin("com.github.ghik" %% "silencer-plugin" % silencerVersion),
    "com.github.ghik" %% "silencer-lib" % silencerVersion % Provided
  )
)
lazy val root = (project in file("."))
  .aggregate(server, db)

lazy val server = (project in file("server"))
  .settings(sharedSettings)
  .settings(
    name := "server",
    testFrameworks += new TestFramework("utest.runner.Framework"),
  )
  .dependsOn(db)

lazy val db = (project in file("db"))
  .settings(sharedSettings)
  .settings(
    name := "usdw-db",
    libraryDependencies ++= Seq(
      "io.getquill" %% "quill-finagle-postgres" % quillVersion,
      "com.opentable.components" % "otj-pg-embedded" % pgEmbeddedVersion % Test,
      "org.liquibase" % "liquibase-core" % "3.6.3" % Test,
    ),
    testFrameworks += new TestFramework("org.usdw.db.DbTestFramework"),
  )

//Liquibase config
import com.permutive.sbtliquibase.SbtLiquibase
enablePlugins(SbtLiquibase)
liquibaseDriver     := "org.postgresql.Driver"
liquibaseChangelog  := file("db/src/main/migrations/changelog.xml")
libraryDependencies += "postgresql" % "postgresql" % "9.1-901-1.jdbc4" % Provided
