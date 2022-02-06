/*
 * This Groovy source file was generated by the Gradle 'init' task.
 */
package uxifier

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import uxifier.models.Component
import uxifier.models.Header
import uxifier.models.HorizontalLayout
import uxifier.models.SocialMedia
import uxifier.models.SocialMediaGroup
import uxifier.models.SocialMediaType
import uxifier.models.WebApplication
import uxifier.models.WebPage

class App {

    static void main(String[] args) {
        ScriptInterpreter dsl = new ScriptInterpreter()
        if (args.length > 0) {
            dsl.eval(new File(args[0]))
        } else {
            System.out.println("/!\\ Missing arg: Please specify the path to a Groovy script file to execute")
        }
    }
}
class ScriptInterpreter{
    private GroovyShell shell
    private CompilerConfiguration configuration
    private Binding binding
    private UXifier basescript

    ScriptInterpreter() {
        binding = new Binding()
        configuration = getDSLConfiguration()
        configuration.setScriptBaseClass("uxifier.UXifier")
        shell = new GroovyShell(configuration)
    }


    void eval(File scriptFile) {
        Script script = shell.parse(scriptFile)

        script.setBinding(binding)

        script.run()
    }

    private static CompilerConfiguration getDSLConfiguration() {
        def secure = new SecureASTCustomizer()
        secure.with {

            closuresAllowed = true

            methodDefinitionAllowed = true

            importsWhitelist = [
                    'java.lang.*'
            ]
            staticImportsWhitelist = []
            staticStarImportsWhitelist = ['uxifier.models.SocialMedia.*']



            constantTypesClassesWhiteList = [
                    int, Integer, Number, Integer.TYPE, String, Object
            ]

            receiversClassesWhiteList = [
                    int, Number, Integer, String, Object
            ]
        }

        def configuration = new CompilerConfiguration()
        configuration.addCompilationCustomizers(secure)

        return configuration
    }
}
class WebApplicationBuilder{

    WebApplication webApplication

    WebApplicationBuilder(WebApplication webApplication) {
        this.webApplication = webApplication
    }

    def name(String appName){
        println("Calling name method in WebApplicationBuilder with ${appName}")
        this.webApplication.name = appName
    }

    def build(){
        return this.webApplication
    }

    def WebPage(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=WebPageBuilder) Closure closure){
        var webPageBuilder = new WebPageBuilder(webApplication)
        def code = closure.rehydrate(webPageBuilder, this, this)//permet de définir que tous les appels de méthodes
        code.resolveStrategy = Closure.DELEGATE_ONLY//à l'intérieur de la closure seront résolus en utilisant le delegate
        code()
        this.webApplication.addWebPage(webPageBuilder.buildPage())
    }
}
class WebPageBuilder implements GenericBuilder{
    String _title
    String _name
    WebApplication webApplication

    WebPageBuilder(WebApplication webApplication){
        this.webApplication = webApplication
    }
    def title(String pageTitle){
        println("Calling title method in WebPageBuilder with ${pageTitle}")
        this._title = pageTitle
    }

    def name(String pageName){
        println("Calling name method in WebPageBuilder with ${pageName}")
        this._name = pageName
    }

    def Header(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=HeaderBuilder) Closure closure){
        var header = new HeaderBuilder()
        def code = closure.rehydrate(header, this, this)//permet de définir que tous les appels de méthodes
        code.resolveStrategy = Closure.DELEGATE_ONLY//à l'intérieur de la closure seront résolus en utilisant le delegate
        code()
        println "Building header ${header.build()}"
        this.componentList.addAll(new Header( header.build()))

    }

    WebPage buildPage(){
        var webPage = new WebPage()

        webPage.name = this._name

        webPage.title = this._title
        webPage.componentList = componentList
        return webPage
    }

}

class HeaderBuilder implements  GenericBuilder{

}


class UXifier extends  Script{
    WebApplication webApplication = new WebApplication()

    @Override
    Object run() {
        return null
    }

    def WebApplication(@DelegatesTo(WebApplicationBuilder) Closure closure){
        var app= new WebApplicationBuilder(webApplication)
        closure.delegate = app
        closure()

        println app.build()

    }


}

trait GenericBuilder{

    List<Component> componentList = new ArrayList<>()

    def HorizontalLayout(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=HorizontalLayoutBuilder) Closure closure){
        var layoutBuilder =  new HorizontalLayoutBuilder()
        def code = closure.rehydrate(layoutBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()

        this.componentList.addAll(new HorizontalLayout( layoutBuilder.build()))

    }

    def SocialMediaGroup(@DelegatesTo(SocialMediaGroupBuiler) Closure closure){
        var socialMediaGroupBuilder = new SocialMediaGroupBuiler()
        def code = closure.rehydrate(socialMediaGroupBuilder, this, this)//permet de définir que tous les appels de méthodes
        code.resolveStrategy = Closure.DELEGATE_ONLY//à l'intérieur de la closure seront résolus en utilisant le delegate
        code()

        this.componentList.addAll(new SocialMediaGroup(socialMediaGroupBuilder.build()))
    }

    List<Component> build(){
        return componentList
    }
}
class SocialMediaGroupBuiler implements  GenericBuilder{
    def SocialMedia(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=SocialMediaBuilder) Closure closure){
        var socialMediaBuilder = new SocialMediaBuilder()
        def code = closure.rehydrate(socialMediaBuilder, this, this)//permet de définir que tous les appels de méthodes
        code.resolveStrategy = Closure.DELEGATE_FIRST//à l'intérieur de la closure seront résolus en utilisant le delegate
        code()
        this.componentList.add(socialMediaBuilder.build())

    }
}

class SocialMediaBuilder {
    SocialMedia socialMedia = new SocialMedia()

    final SocialMediaType Facebook = SocialMediaType.FACEBOOK
    final SocialMediaType Pinterest = SocialMediaType.PINTEREST
    final SocialMediaType Instagram = SocialMediaType.INSTAGRAM
    final SocialMediaType LinkedIn = SocialMediaType.LINKEDIN

    def type(SocialMediaType socialMediaType ){
        this.socialMedia.type  = socialMediaType
    }

    def url(String urlLink){
        this.socialMedia.url = urlLink
    }

    SocialMedia build(){
        return this.socialMedia
    }
}
class HorizontalLayoutBuilder implements GenericBuilder{

}