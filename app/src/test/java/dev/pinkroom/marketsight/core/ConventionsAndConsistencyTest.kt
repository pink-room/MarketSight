package dev.pinkroom.marketsight.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import com.lemonappdev.konsist.api.ext.list.properties
import com.lemonappdev.konsist.api.ext.list.withAnnotationOf
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withParentClassOf
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class ConventionsAndConsistencyTest {
    private val baseSourceName = "dev.pinkroom.marketsight"
    @Test
    fun `Clean Architecture layers have correct dependencies`() {
        Konsist
            .scopeFromProduction()
            .assertArchitecture {
                // Define layers
                val domain = Layer("domain", "$baseSourceName.domain..")
                val presentation = Layer("presentation", "$baseSourceName.presentation..")
                val data = Layer("data", "$baseSourceName.data..")

                // Define architecture assertions
                data.dependsOn(domain)
                domain.dependsOnNothing()
                presentation.dependsOn(domain)
            }
    }

    @Test
    fun `Validate data_source package all classes has name ending with 'DataSource'`() {
        Konsist
            .scopeFromPackage(packagee = "$baseSourceName.data.data_source..")
            .classes()
            .assertTrue { it.hasNameEndingWith("DataSource") || it.hasNameEndingWith("DataSourceTest") }

        Konsist
            .scopeFromProduction()
            .classes()
            .withNameEndingWith("DataSource")
            .assertTrue { it.resideInPackage("$baseSourceName.data.data_source..") }
    }

    @Test
    fun `Classes with name ending 'Database' reside in correct package`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("Database")
            .assertTrue { it.resideInPackage("$baseSourceName.data.local..") }
    }

    @Test
    fun `Validate Entity classes reside in correct package`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("Entity")
            .assertTrue { it.resideInPackage("$baseSourceName.data.local.entity..") }
    }

    @Test
    fun `Validate Dao interfaces reside in correct package`() {
        Konsist
            .scopeFromProject()
            .interfaces()
            .withNameEndingWith("Dao")
            .assertTrue { it.resideInPackage("$baseSourceName.data.local.dao..") }
    }

    @Test
    fun `Validate mappers fun reside in correct package`() {
        Konsist
            .scopeFromProject()
            .files
            .withNameEndingWith("Mapper")
            .assertTrue { it.hasPackage ("$baseSourceName.data.mapper..") }
    }

    @Test
    fun `Validate Dto classes reside in correct package`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("Dto")
            .assertTrue { it.resideInPackage("$baseSourceName.data.remote.model.dto..") }
    }

    @Test
    fun `Validate Api interfaces reside in correct package`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("Api")
            .assertTrue { it.resideInPackage("$baseSourceName.data.remote..") }
    }

    @Test
    fun `Validate Services interfaces reside in correct package`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("Service")
            .assertTrue { it.resideInPackage("$baseSourceName.data.remote..") }
    }

    @Test
    fun `Validate UseCase classes reside in correct package`() {
        Konsist
            .scopeFromProduction()
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue { it.resideInPackage("$baseSourceName.domain.use_case..") }
    }

    @Test
    fun `Validate model layer depends on nothing`() {
        Konsist
            .scopeFromProduction()
            .assertArchitecture {
                val model = Layer("domain", "$baseSourceName.domain.model..")
                model.dependsOnNothing()
            }
    }

    @Test
    fun `Interfaces extending 'Repository' reside in correct package`() {
        Konsist
            .scopeFromProduction()
            .interfaces()
            .withNameEndingWith("Repository")
            .assertTrue { it.resideInPackage("$baseSourceName.domain.repository..") }
    }

    @Test
    fun `Classes that implements Repository interfaces reside in correct package`() {
        Konsist
            .scopeFromProduction()
            .classes()
            .withNameEndingWith("RepositoryImp")
            .assertTrue { it.resideInPackage("$baseSourceName.data.repository..") }
    }

    @Test
    fun `Classes extending ViewModel should have 'ViewModel' suffix`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withParentClassOf(ViewModel::class)
            .assertTrue { it.name.endsWith("ViewModel") }
    }

    @Test
    fun `Classes extending ViewModel reside in correct package`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withParentClassOf(ViewModel::class)
            .assertTrue { it.resideInPackage("$baseSourceName.presentation..") }
    }

    @Test
    fun `Every 'ViewModel' public property has 'Flow' or 'StateFlow' type`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withParentClassOf(ViewModel::class)
            .properties()
            .assertTrue {
                it.hasPublicOrDefaultModifier && it.hasType { type -> type.name == "kotlinx.coroutines.flow.." }
            }
    }

    @Test
    fun `All JetPack Compose screens contain 'Screen' in method name has 'Composable' annotation`() {
        Konsist
            .scopeFromProject()
            .functions()
            .withNameEndingWith("Screen")
            .assertTrue { it.hasAnnotationOf(Composable::class) }
    }

    @Test
    fun `No class should use Android util logging`() {
        Konsist
            .scopeFromProject()
            .files
            .assertFalse { it.hasImport { import -> import.name == "android.util.Log" } }
    }

    @Test
    fun `All JetPack Compose previews contain 'Preview' in method name`() {
        Konsist
            .scopeFromProject()
            .functions()
            .withAnnotationOf(Preview::class)
            .assertTrue { it.hasNameContaining("Preview") }
    }
}