package siosio.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.DefaultExecAction
import org.gradle.process.internal.ExecAction
import org.gradle.util.ConfigureUtil
import org.gradle.api.GradleException

/**
 * Sphinxのビルドを行うためのタスク。
 *
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

  /** ビルドを実行するアクション */
  private SphinxBuildAction action

  /**
   * コンストラクタ。
   */
  Sphinx() {
    action = new SphinxBuildAction(getServices().get(FileResolver.class))
  }

  /**
   * ビルドを行う。
   */
  @TaskAction
  def build() {
    action.execute()
  }

  /**
   * ビルドコマンドを設定する。
   *
   * デフォルト値は、sphinx-build
   * @param buildCommand コマンド名(絶対パスなどでの指定も可)
   * @return 自分自身
   */
  def setBuildCommand(def buildCommand) {
    action.buildCommand = buildCommand
    this
  }

  /**
   * ターゲットを設定する。
   *
   * デフォルト値は、html
   * @param target ターゲット名(例えば、singlehtmlやepubなど)
   * @return 自分自身
   */
  def setTarget(def target) {
    action.target = target
    this
  }

  /**
   * ドキュメントルート(conf.pyのあるディレクトリ)を設定する。
   *
   * デフォルト値は、document
   *
   * @param documentRoot ドキュメントルート
   * @return 自分自身
   */
  def setDocumentRoot(def documentRoot) {
    action.documentRoot = documentRoot
    this
  }

  /**
   * 出力先のディレクトリを設定する。
   *
   * デフォルト値は、build/document
   *
   * @param output 出力先のディレクトリ
   * @return 自分自身
   */
  def setOutput(def output) {
    action.output = output
    this
  }

  /**
   * オプションを設定する。
   *
   * デフォルトはなし。
   *
   * @param options オプションを配列で設定する。
   * @return 自分自身
   */
  def setOptions(String... options) {
    action.options = options
    this
  }
}

/**
 * Sphinxプラグイン。
 *
 * このプラグインでは、{@link Project}に対してsphinxBuildメソッドを追加する。
 * これにより、タスク内でsphinxBuildメソッドを使用して任意のタイミングでドキュメントのビルドを行うこともできる。
 */
class SphinxPlugin implements Plugin<Project> {

  @Override
  void apply(Project target) {
    target.setStatus(100)
    target.metaClass.sphinxBuild {closure ->
      def action = new SphinxBuildAction(target.fileResolver)
      ConfigureUtil.configure(closure, action)
      action.execute()
    }
  }
}

/**
 * Sphinxのビルドを行うクラス。
 *
 * デフォルト値として以下の設定値を持つ。
 *
 * <pre>
 * ビルドコマンド      -> sphix-build
 * ターゲット          -> html
 * ドキュメントルート  -> document
 * 出力先ディレクトリ  -> build/document
 * ビルドオプション    -> なし
 * </pre>
 *
 */
class SphinxBuildAction {

  private def ExecAction action;
  private def buildCommand = "sphinx-build"
  private def target = "html"
  private def documentRoot = "document"
  private def output = "build/document"
  private List<String> options = []

  SphinxBuildAction(def fileResolver) {
    action = new DefaultExecAction(fileResolver)
  }

  def execute() {
    action.workingDir = documentRoot
    action.executable = buildCommand
    action.args = ['-b', target, * options, documentRoot, output]
    println "sphinx-build command line option:${action.args.join(' ')}"

    def wrapper = new OutputStreamWrapper(stream: action.errorOutput)
    try {
      action.setErrorOutput(wrapper)
      def result = action.execute()
      if (wrapper.warnings) {
        throw new GradleException("sphinx build was output warning. document root = [${documentRoot}]")
      }
      result
    } finally {
      wrapper.close()
    }
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

  def options(String... options) {
    this.options = options as List<String>
    this
  }
}

class OutputStreamWrapper extends OutputStream {

  OutputStream stream

  List warnings = []

  OutputStreamWrapper() {
  }

  @Override
  void write(int b) {
    stream.write(b)
  }

  @Override
  void write(byte[] b) {
    stream.write(b)
  }

  @Override
  void write(byte[] b, int off, int len) {
    stream.write(b, off, len)
    def str = new String(b, off, len)
    if (str.contains('WARNING')) {
      warnings << str
    }
  }

  @Override
  void close() {
    stream.close()
  }

  @Override
  void flush() {
    stream.flush()
  }
}

