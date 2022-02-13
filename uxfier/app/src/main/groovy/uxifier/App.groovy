/*
 * This Groovy source file was generated by the Gradle 'init' task.
 */
package uxifier

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import uxifier.models.Cart
import uxifier.models.Component
import uxifier.models.DeletableAnswer
import uxifier.models.DeliveryInCart
import uxifier.models.Header
import uxifier.models.HorizontalLayout
import uxifier.models.MiniDescription
import uxifier.models.Poster
import uxifier.models.ProductInCart
import uxifier.models.PromoCode
import uxifier.models.QuantityInCart
import uxifier.models.QuantityInCartEditionMode
import uxifier.models.Remark
import uxifier.models.SocialMedia
import uxifier.models.SocialMediaGroup
import uxifier.models.SocialMediaType
import uxifier.models.SubTotal
import uxifier.models.Summary
import uxifier.models.Total
import uxifier.models.WebApplication
import uxifier.models.WebPage
import uxifier.models.visitors.ApplicationModelVisitorVueJS

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
        if(appName.contains(' ')){
            println "Invalid name for application '${appName}' : it should not have spaces"
            System.exit(1)
        }
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

    def Cart(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=CartBuilder) Closure closure){
        var cart = new CartBuilder()
        def code = closure.rehydrate(cart, this, this)//permet de définir que tous les appels de méthodes
        code.resolveStrategy = Closure.DELEGATE_ONLY//à l'intérieur de la closure seront résolus en utilisant le delegate
        code()
        println "Building Cart ${cart.build()}"
        this.componentList.addAll(cart.build())
        //this.componentList.addAll(new HorizontalLayoutBuilder( cart.build() ))
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

class PosterBuilder {
    var poster = new Poster()

    Poster build(){
        return poster
    }
}

class MiniDescriptionBuilder {
    var miniDescription = new MiniDescription()

    MiniDescription build(){
        return miniDescription
    }
}

class QuantityBuilder {

    final QuantityInCartEditionMode Default = QuantityInCartEditionMode.Default

    var quantityInCart = new QuantityInCart()

    def editionMode(QuantityInCartEditionMode quantityInCartEditionMode){
        quantityInCart.setQuantityInCartEditionMode(quantityInCartEditionMode)
    }

    QuantityInCart build(){
        return miniDescription
    }
}

class ProductInCartBuilder{

    final DeletableAnswer yes = DeletableAnswer.yes
    final DeletableAnswer no = DeletableAnswer.no

    ProductInCart productInCart = new ProductInCart();

    def deletable(DeletableAnswer deletableAnswer){
        if (deletableAnswer == DeletableAnswer.yes) productInCart.enableDeleteable()
    }

    def Poster(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=PosterBuilder) Closure closure) {
        var layoutBuilder =  new PosterBuilder()
        def code = closure.rehydrate(layoutBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
    def MiniDescription(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=MiniDescriptionBuilder) Closure closure) {
        var layoutBuilder =  new MiniDescriptionBuilder()
        def code = closure.rehydrate(layoutBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
    def Quantity(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=QuantityBuilder) Closure closure) {
        var layoutBuilder =  new QuantityBuilder()
        def code = closure.rehydrate(layoutBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
    def total(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=TotalBuilder) Closure closure){
        var layoutBuilder =  new TotalBuilder()
        def code = closure.rehydrate(layoutBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        productInCart.addTotalComponent();
    }
    ProductInCart build(){
        return productInCart
    }
}

class ProductsInCartBuilder{
    ProductInCart productInCart = new ProductInCart()

    def Product(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=ProductInCartBuilder) Closure closure) {
        var layoutBuilder =  new ProductInCartBuilder()
        def code = closure.rehydrate(layoutBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        productInCart = layoutBuilder.build()
    }

    ProductInCart build(){
        return productInCart
    }
}

trait TitleBuilder{
    String content;

    def title(String content){
        this.content = content
    }
}
trait labelBuilder{
    String label;

    def label(String content){
        this.label = content
    }
}

class PromoCodeBuilder implements labelBuilder{
    PromoCode promoCode = new PromoCode()

    PromoCode build(){
        promoCode.setLabel(label)
        return promoCode
    }
}

class RemarkBuilder implements labelBuilder{
    Remark remark = new Remark()

    Remark build(){
        remark.setLabel(label)
        return remark
    }
}

class SubTotalBuilder implements labelBuilder{
    SubTotal subTotal = new SubTotal()

    SubTotal build(){
        subTotal.setLabel(label)
        return subTotal
    }
}

class DeliveryInCartBuilder implements labelBuilder {
    DeliveryInCart deliveryInCart = new DeliveryInCart()
    def defaultValue(Integer defaultValue){
        deliveryInCart.setDefaultValue(defaultValue)
    }

    DeliveryInCart build() {
        deliveryInCart.setLabel(label)
        return deliveryInCart
    }
}

class TotalBuilder implements labelBuilder{
    Total total = new Total()

    Total build(){
        total.setLabel(label)
        return total
    }
}

class SummaryBuilder{
    Summary summary = new Summary()

    def label(String content){
        summary.setLabel(content)
    }

    def subTotal(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=SubTotalBuilder) Closure closure){
        var subTotalBuilder =  new SubTotalBuilder()
        def code = closure.rehydrate(subTotalBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        summary.setSubTotal(subTotalBuilder.build())
    }

    def Delivery(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=DeliveryInCartBuilder) Closure closure){
        var deliveryInCartBuilder =  new DeliveryInCartBuilder()
        def code = closure.rehydrate(deliveryInCartBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        summary.setDelivery(deliveryInCartBuilder.build())
    }

    def total(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=TotalBuilder) Closure closure){
        var totalBuilder =  new TotalBuilder()
        def code = closure.rehydrate(totalBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        summary.setTotal(totalBuilder.build())
    }
    Summary build(){
        return summary
    }
}

class CartBuilder implements TitleBuilder{

    Cart cart = new Cart()

    def Products(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=ProductsInCartBuilder) Closure closure){
        var productsInCartBuilderBuilder =  new ProductsInCartBuilder()
        def code = closure.rehydrate(productsInCartBuilderBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        cart.setProductInCart(productsInCartBuilderBuilder.build())
    }
    def PromoCode(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=PromoCodeBuilder) Closure closure){
        var promoCodeBuilder =  new PromoCodeBuilder()
        def code = closure.rehydrate(promoCodeBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        cart.setPromoCode(promoCodeBuilder.build())
    }
    def Remark(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=RemarkBuilder) Closure closure){
        var remarkBuilder =  new RemarkBuilder()
        def code = closure.rehydrate(remarkBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        cart.setRemark(remarkBuilder.build())
    }
    def Summary(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=SummaryBuilder) Closure closure){
        var summaryBuilder =  new SummaryBuilder()
        def code = closure.rehydrate(summaryBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        cart.setSummary(summaryBuilder.build())
    }
    Cart build() {
        return cart
    }
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

        var application = app.build()

        println application

        var applicationVisitor = new ApplicationModelVisitorVueJS()

        applicationVisitor.visit(application)

        println applicationVisitor.vueProject
        applicationVisitor.vueProject.toCode()


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

class SocialMediaGroupBuiler implements GenericBuilder{
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

    final SocialMediaType Facebook = SocialMediaType.Facebook
    final SocialMediaType Pinterest = SocialMediaType.Pinterest
    final SocialMediaType Instagram = SocialMediaType.Instagram
    final SocialMediaType LinkedIn = SocialMediaType.LinkedIn

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
