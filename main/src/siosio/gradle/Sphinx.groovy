package siosio.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.DefaultExecAction
import org.gradle.process.internal.ExecAction

class Sphinx extends DefaultTask {

  private ExecAction action;
  private buildCommand = "sphinx-build"
  private target = "html"
  private documentRoot = "document"
  private output = "build/document"

  Sphinx() {
    action = new DefaultExecAction(getServices().get(FileResolver.class))
  }

  @TaskAction
  def build() {
    action.executable = buildCommand
    action.args = ['-b', target, documentRoot, output]
    println "sphinx-build command line option:${action.args.join(' ')}"
    def execResult = action.execute()
  }

  def setBuildCommand(def buildCommand) {
    this.buildCommand = buildCommand
    this
  }

  def setTarget(def target) {
    this.target = target
    this
  }

  def setDocumentRoot(def documentRoot) {
    this.documentRoot = documentRoot
    this
  }

  def setOutput(def output) {
    this.output = output
    this
  }

}
