package pl.droidsonroids.gradle.jenkins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.ddmlib.DdmPreferences
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

public class JenkinsPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        DdmPreferences.setTimeOut(30000)
        addJavacXlint(project)

        project.allprojects { Project subproject ->
            subproject.plugins.withType(AppPlugin) {
                subproject.extensions.create('jenkins', JenkinsExtension)
                addJenkinsReleaseBuildType(subproject)
                subproject.extensions.getByType(AppExtension).buildTypes.each {
                    it.ext.isMonkeyTestable = false
                    it.ext.monkeyTestable = { boolean isMonkeyTestable -> it.isMonkeyTestable = isMonkeyTestable }
                }
                subproject.tasks.create('connectedMonkeyTest', MonkeyTask, { it.subproject = subproject })
            }
        }
    }

    def addJenkinsReleaseBuildType(def subproject) {
        def android = subproject.extensions.getByType(AppExtension)
        android.signingConfigs {
            jenkinsRelease {
                storeFile new File("$System.env.HOME/.android/debug.keystore")
                storePassword 'android'
                keyAlias 'androiddebugkey'
                keyPassword 'android'
            }
        }
        android.buildTypes {
            jenkinsRelease {
                minifyEnabled true
                proguardFiles android.getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
                signingConfig android.signingConfigs.jenkinsRelease
            }
        }
    }

    def addJavacXlint(Project project) {
        project.allprojects {
            gradle.projectsEvaluated {
                tasks.withType(JavaCompile) {
                    options.compilerArgs << "-Xlint"
                }
            }
        }
    }
}