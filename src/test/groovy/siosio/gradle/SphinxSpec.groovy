package siosio.gradle

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class SphinxSpec extends Specification {

  static final TASK_NAME = 'makeDoc'

  Project project

  String testOutputDir = 'src/test/data/output'

  def setup() {
    project = ProjectBuilder.builder().build()
    new AntBuilder().delete(dir: testOutputDir)
  }

  def "add Sphinx default property task"() {
    expect:
    findTask() == null

    when:
    project.task(TASK_NAME, type: Sphinx) {
    }

    then:
    def task = findTask()
    task != null
    task.documentRoot == "${project.projectDir}/doc"
    task.output == "${project.buildDir}/doc"
    task.command == 'sphinx-build'
    task.format == 'html'
  }

  def "add Sphinx task"() {
    expect:
    findTask() == null

    when:
    project.task(TASK_NAME, type: Sphinx) {
      documentRoot = 'doc'
      output = 'build/doc/html'
      command = './sphinx-build'
      format = 'singlehtml'
    }

    then:
    def task = findTask()
    task != null
    task.documentRoot == 'doc'
    task.output == 'build/doc/html'
    task.command == './sphinx-build'
    task.format == 'singlehtml'
  }

  def "build Sphinx document"() {
    expect:
    findTask() == null
    !new File(testOutputDir).exists()

    when:
    Task task = project.task(TASK_NAME, type: Sphinx){
      documentRoot = 'src/test/data/input'
      output = 'src/test/data/output'
      workingDir = new File('.').absolutePath
    }
    task.build()

    then:
    new File("$testOutputDir/index.html").exists()

  }

  def "build error"() {
    expect:
    findTask() == null
    !new File(testOutputDir).exists()

    when:
    Task task = project.task(TASK_NAME, type: Sphinx){
      documentRoot = 'src/test/data/notfound'
      output = 'src/test/data/output'
      workingDir = new File('.').absolutePath
    }
    task.build()

    then:
    findTask() != null
    thrown (GradleException)
  }

  def findTask() {
    project.tasks.findByName(TASK_NAME)
  }
}

