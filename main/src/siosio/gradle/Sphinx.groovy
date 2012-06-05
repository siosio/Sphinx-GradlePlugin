package siosio.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.DefaultExecAction
import org.gradle.process.internal.ExecAction
import org.gradle.util.ConfigureUtil

class Sphinx extends DefaultTask {

  private SphinxBuildAction action

  Sphinx() {
    action = new SphinxBuildAction(getServices().get(FileResolver.class))
  }

  @TaskAction
  def build() {
    action.executable = buildCommand
    action.args = ['-b', target, documentRoot, output]
  }

  def setBuildCommand(def buildCommand) {
    action.buildCommand = buildCommand
    this
  }

  def setTarget(def target) {
    action.target = target
    this
  }

  def setDocumentRoot(def documentRoot) {
    action.documentRoot = documentRoot
    this
  }

  def setOutput(def output) {
    action.output = output
    this
  }

  def setOptions(def options) {
    action.options = options
    this
  }
}

class SphinxPlugin implements Plugin<Project> {

  @Override
  void apply(Project target) {
    target.metaClass.sphinxBuild {closure ->
      def action = new SphinxBuildAction(target.fileResolver)
      ConfigureUtil.configure(closure, action)
      action.execute()
    }
  }
}
class SphinxBuildAction {

  def ExecAction action;
  def buildCommand = "sphinx-build"
  def target = "html"
  def documentRoot = "document"
  def output = "build/document"
  def options = []

  SphinxBuildAction(def fileResolver) {
    action = new DefaultExecAction(fileResolver)
  }

  def execute() {
    action.executable = buildCommand
    def args = ['-b', target]
    args += options
    args += [documentRoot, output]
    action.args = args
    println "sphinx-build command line option:${action.args.join(' ')}"
    action.execute()
  }

  def target(String target) {
    this.target = target
    this
  }

  def buildCommand(def buildCommand) {
    this.buildCommand = buildCommand
    this
  }

  def documentRoot(def documentRoot) {
    this.documentRoot = documentRoot
    this
  }

  def output(def output) {
    this.output = output
    this
  }

  def options(def options) {
    this.options = options
    this
  }
}

