package siosio.gradle
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
/**
 * Sphinxのビルドを行うためのタスク。
 * <p/>
 * 使用例。
 * <pre autoTested=''>
 * // デフォルト設定を使用する場合
 * task buildDocument(type : Sphinx) {
 * }
 *
 * // 設定を変更する場合
 * task buildDocument(type : Sphinx) {
 *   target 'singlehtml'     // singlehtml形式で出力
 *   documentRoot 'doc'      // ビルド対象は、「doc」配下
 *   output  'output/doc'    // ビルド結果は、「output/doc」に出力
 * }
 * </pre>
 */
class Sphinx extends DefaultTask {

  @InputDirectory
  String documentRoot = "${project.projectDir}/doc"

  @OutputDirectory
  String output = "${project.buildDir}/doc"

  String workingDir = project.projectDir

  String format = 'html'

  String command = 'sphinx-build'

  @TaskAction
  void build() {
    String _workingDir = workingDir
    project.exec {
      workingDir = _workingDir
      executable = command
      args = ['-b', format, documentRoot, output]
    }
  }
}

