SphinxドキュメントをビルドするためのGradle拡張プラグイン

このプラグインでは、Sphinxタスクやメソッド呼び出しでsphinx-buildを行うことが出来ます。

インストール方法
================
1.src配下を、gradleを使用するプロジェクトのbuildSrc配下に配置する。
 配置後のイメージは以下のようになります。
 $プロジェクトルーツ$/buildSrc/src/main/groovy/siosio/gradle/Sphinx.groovy

2.resource配下を、プロジェクトbuildSrc配下に配置する。
 配置後のイメージは以下のようになります。
 $プロジェクトルーツ$/buildSrc/src/main/resources/META-INF/gradle-plugins/Sphinx.properties

使用方法
=========
本プラグインでは、以下の値をデフォルト値として定義しているので、デフォルト値のままビルドを行う場合には、
非常に簡単にビルドを行うことが出来ます。

* buildCommand(ビルド時に使用するコマンド)

    sphinx-build

* target(出力形式(html、singlehtml、epubなど)

    html
* documentRoot(ドキュメントのルートディレクトリ(conf.pyのあるディレクトリ))

    document
* output(ビルドしたドキュメントの出力先)

    build/document

タスクとして使用する
-----------------------
__デフォルト設定で実行する場合__

    task buildDocument(type : Sphinx) {
    }

__デフォルト設定を変更して実行する場合__

    task buildDocument(type : Sphinx) {
        target 'singlehtml'     // singlehtml形式で出力
        documentRoot 'doc'      // ビルド対象は、「doc」配下
        output  'output/doc'    // ビルド結果は、「output/doc」に出力
    }

メソッド呼出として使用する
--------------------------
メソッド呼出で使用する場合は、Sphinxプラグインを使用します。

__デフォルト設定で実行する場合__

    apply plugin : 'Sphinx'

    task documentBuild() << {
       sphinxBuild {}
    }


__デフォルト設定を変更して実行する場合__
メソッド呼出の場合、以下の例のように1タスク内で複数のドキュメントをビルドすることも出来ます。

    apply plugin : 'Sphinx'

    task documentBuild() << {
       sphinxBuild {
           target 'singlehtml'     // singlehtml形式で出力
           documentRoot 'doc'      // ビルド対象は、「doc」配下
           output  'output/doc'    // ビルド結果は、「output/doc」に出力
       }

       sphinxBuild {
           target 'singlehtml'      // singlehtml形式で出力
           documentRoot 'doc2'      // ビルド対象は、「doc2」配下
           output  'output/doc2'    // ビルド結果は、「output/doc2」に出力
       }
    }


