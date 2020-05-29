lazy val finchVersion = "0.32.1"
lazy val circeVersion = "0.13.0"
lazy val refinedVersion = "0.9.13"
lazy val quillVersion = "3.5.1"
lazy val finaglePostgresVersion = "0.13.0-SNAPSHOT"
lazy val pgEmbeddedVersion = "0.13.3"
lazy val liquibaseVersion = "3.8.8"
lazy val munitVersion = "0.7.3"
lazy val scalacheckVersion = "1.14.3"

lazy val scribeVersion = "2.7.12"
lazy val acyclicVersion = "0.2.0"
lazy val silencerVersion = "1.6.0"
lazy val kindProjectorVersion = "0.11.0"
lazy val betterMonadicForVersion = "0.3.1"
lazy val scalaTypedHoleVersion = "0.1.3"
lazy val splainVersion = "0.5.6"

lazy val sharedSettings = Seq(
  organization := "sungkmi",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.11",

  autoCompilerPlugins := true,
  addCompilerPlugin("com.lihaoyi" %% "acyclic" % acyclicVersion),
  addCompilerPlugin("io.tryp" %% "splain" % splainVersion cross CrossVersion.patch),
  addCompilerPlugin("org.typelevel" %% "kind-projector" % kindProjectorVersion cross CrossVersion.full),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % betterMonadicForVersion),
  addCompilerPlugin("com.github.cb372" % "scala-typed-holes" % scalaTypedHoleVersion cross CrossVersion.full),

  resolvers += Resolver.sonatypeRepo("releases"),

  // for finagle-postgres
  resolvers += Resolver.sonatypeRepo("snapshots"),

  cancelable in Global := true,

  // scala-typed-holes set log-level info
  scalacOptions += "-P:typed-holes:log-level:info",

  // acyclic
  scalacOptions += "-P:acyclic:force",

  scalacOptions ++= Seq(
    "-deprecation",                              // Emit warning and location for usages of deprecated APIs.
    "-explaintypes",                             // Explain type errors in more detail.
    "-feature",                                  // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials",                    // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros",             // Allow macro definition (besides implementation and application)
    "-language:higherKinds",                     // Allow higher-kinded types
    "-language:implicitConversions",             // Allow definition of implicit functions called views
    "-unchecked",                                // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit",                               // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfatal-warnings",                          // Fail the compilation if there are any warnings.
    "-Xlint:adapted-args",                       // Warn if an argument list is modified to match the receiver.
    "-Xlint:constant",                           // Evaluation of a constant arithmetic expression results in an error.
    "-Xlint:delayedinit-select",                 // Selecting member of DelayedInit.
    "-Xlint:doc-detached",                       // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible",                       // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any",                          // Warn when a type argument is inferred to be `Any`.
    "-Xlint:missing-interpolator",               // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-override",                   // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit",                       // Warn when nullary methods return Unit.
    "-Xlint:option-implicit",                    // Option.apply used implicit view.
    "-Xlint:package-object-classes",             // Class or object defined in package object.
    "-Xlint:poly-implicit-overload",             // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow",                     // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align",                        // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow",              // A local type parameter shadows a type already in scope.
    "-Ywarn-dead-code",                          // Warn when dead code is identified.
    "-Ywarn-extra-implicit",                     // Warn when more than one implicit parameter section is defined.
    "-Ywarn-numeric-widen",                      // Warn when numerics are widened.
    "-Ywarn-unused:implicits",                   // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports",                     // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals",                      // Warn if a local definition is unused.
    "-Ywarn-unused:params",                      // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars",                     // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates",                    // Warn if a private member is unused.
    "-Ywarn-value-discard",                      // Warn when non-Unit expression results are unused.
    "-Ybackend-parallelism", "8",                // Enable paralellisation — change to desired number!
    "-Ycache-plugin-class-loader:last-modified", // Enables caching of classloaders for compiler plugins
    "-Ycache-macro-class-loader:last-modified",  // and macro definitions. This can lead to performance improvements.
  ),

  scalacOptions in (Compile, console) ~= (_.filterNot(Set(
    "-Ywarn-unused:imports",
    "-Xfatal-warnings"
  ))),

  // wartremover
//  wartremoverWarnings in (Compile, compile) ++= Warts.allBut(Wart.Any, Wart.Nothing, Wart.StringPlusAny),

  testFrameworks += new TestFramework("munit.Framework"),

  // assembly plugin related
  assemblyMergeStrategy in assembly := {
    case "BUILD" => MergeStrategy.discard
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case other => MergeStrategy.defaultMergeStrategy(other)
  },

  libraryDependencies ++= Seq(
    "io.circe" %%% "circe-core" % circeVersion,
    "io.circe" %%% "circe-generic" % circeVersion,
    "io.circe" %%% "circe-parser" % circeVersion,
    "com.outr" %%% "scribe" % scribeVersion,
    "eu.timepit" %%% "refined" % refinedVersion,
    "eu.timepit" %%% "refined-cats" % refinedVersion,
    "org.scalameta" %%% "munit" % munitVersion % Test,
    "org.scalameta" %%% "munit-scalacheck" % munitVersion % Test,
    "org.scalacheck" %%% "scalacheck" % scalacheckVersion % Test,
    "eu.timepit" %%% "refined-scalacheck" % refinedVersion % Test,
    compilerPlugin("com.github.ghik" %% "silencer-plugin" % silencerVersion cross CrossVersion.full),
    "com.github.ghik" %% "silencer-lib" % silencerVersion % Provided cross CrossVersion.full,
  )
)

lazy val root = (project in file("."))
  .aggregate(server, db)

lazy val server = (project in file("server"))
  .settings(sharedSettings)
  .settings(
    name := "server",

    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finchx-core"  % finchVersion,
      "com.github.finagle" %% "finchx-circe"  % finchVersion,
    ),
  )
  .dependsOn(db)

lazy val db = (project in file("db"))
  .settings(sharedSettings)
  .settings(
    name := "db",
    libraryDependencies ++= Seq(
      "io.getquill" %% "quill-finagle-postgres" % quillVersion,
      "com.opentable.components" % "otj-pg-embedded" % pgEmbeddedVersion % Test,
      "org.liquibase" % "liquibase-core" % "3.6.3" % Test,
    ),
  )

//Liquibase config
import com.permutive.sbtliquibase.SbtLiquibase
enablePlugins(SbtLiquibase)
liquibaseDriver     := "org.postgresql.Driver"
liquibaseChangelog  := file("db/src/main/migrations/changelog.xml")
libraryDependencies += "postgresql" % "postgresql" % "9.1-901-1.jdbc4" % Provided
