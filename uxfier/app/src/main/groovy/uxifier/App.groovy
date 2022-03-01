/*
 * This Groovy source file was generated by the Gradle 'init' task.
 */
package uxifier

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import uxifier.models.*
import uxifier.models.visitors.ApplicationModelVisitorVueJS

class App {

    static void main(String[] args) {
        ScriptInterpreter dsl = new ScriptInterpreter()
        if (args.length > 0) {
            dsl.eval(new File(args[0]))
        } else {
            System.out.println("Missing arg: Please specify the path to a Groovy script file to execute")
        }
    }
}

class ScriptInterpreter {
    private GroovyShell shell
    private CompilerConfiguration configuration
    private Binding binding
    private UXifier basescript

    ScriptInterpreter() {
        binding = new Binding()
        configuration = getDSLConfiguration()
        configuration.setScriptBaseClass("uxifier.UXifier")
        shell = new GroovyShell(binding, configuration)
    }


    void eval(File scriptFile) {
        Script script = shell.parse(scriptFile)

        script.run()
    }

    private static CompilerConfiguration getDSLConfiguration() {
        var secure = new SecureASTCustomizer()
        secure.setAllowedStaticStarImports(['uxifier.models.SocialMediaType', 'uxifier.models.NavigationMenuType', 'uxifier.models.PrintingType'])
        secure.setClosuresAllowed(true)

        /*secure.with {

            closuresAllowed = true

            methodDefinitionAllowed = true

            constantTypesClassesWhiteList = [
                    int, Integer, Number, Integer.TYPE, String, Object, boolean
            ]

            receiversClassesWhiteList = [
                    int, Number, Integer, String, Object
            ]
        }*/

        def configuration = new CompilerConfiguration()
        var icz = new ImportCustomizer()
        icz = icz.addStaticStars('uxifier.models.SocialMediaType', 'uxifier.models.NavigationMenuType')
        configuration.addCompilationCustomizers(icz, secure)
        return configuration
    }
}

class WebApplicationBuilder {

    WebApplication webApplication

    WebApplicationBuilder(WebApplication webApplication) {
        this.webApplication = webApplication
    }

    def name(String appName) {
        if (appName.contains(' ')) {
            println "Invalid name for application '${appName}' : it should not have spaces"
            System.exit(1)
        }
        this.webApplication.name = appName
    }

    def pageTitle(String title) {
        this.webApplication.title = title
    }

    def build() {
        return this.webApplication
    }

    def NavigationMenu(@DelegatesTo(NavigationMenuBuilder) Closure closure) {
        var builder = new NavigationMenuBuilder()

        var code = closure.rehydrate(builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        this.webApplication.navigationMenu = new NavigationMenu(builder.componentList, builder.getMenuType(), builder.applicationName)

    }

    def WebPage(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = WebPageBuilder) Closure closure) {
        var webPageBuilder = new WebPageBuilder(webApplication)
        def code = closure.rehydrate(webPageBuilder, this, this)//permet de définir que tous les appels de méthodes
        code.resolveStrategy = Closure.DELEGATE_FIRST
//à l'intérieur de la closure seront résolus en utilisant le delegate
        code()
        this.webApplication.addWebPage(webPageBuilder.buildPage())
    }
}

class WebPageBuilder implements GenericBuilder {
    String _title
    String _name
    WebApplication webApplication

    WebPageBuilder(WebApplication webApplication) {
        this.webApplication = webApplication
    }

    def title(String pageTitle) {
        this._title = pageTitle
    }

    def name(String pageName) {
        this._name = pageName
    }


    def Header(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = HeaderBuilder) Closure closure) {
        var header = new HeaderBuilder()
        def code = closure.rehydrate(header, this, this)//permet de définir que tous les appels de méthodes
        code.resolveStrategy = Closure.DELEGATE_FIRST
//à l'intérieur de la closure seront résolus en utilisant le delegate
        code()
        this.componentList.addAll(new Header(header.build()))

    }

    def Cart(@DelegatesTo(strategy=Closure.DELEGATE_FIRST, value=CartBuilder) Closure closure){
        var cart = new CartBuilder()
        def code = closure.rehydrate(cart, this, this)//permet de définir que tous les appels de méthodes
        code.resolveStrategy = Closure.DELEGATE_FIRST//à l'intérieur de la closure seront résolus en utilisant le delegate
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


class HeaderBuilder implements GenericBuilder {

}

class PosterBuilder {

    var poster = new Poster()

    def smallDimensions(int width,int height){
        poster.setSmallWidth(width)
        poster.setSmallHeight(height)
    }

    def largeDimensions(int width,int height){
        poster.setSmallWidth(width)
        poster.setSmallHeight(height)
    }

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

    final EditableAnswer yes = EditableAnswer.yes
    final EditableAnswer no = EditableAnswer.no

    var quantityInCart = new QuantityInCart()

    def editable(EditableAnswer editableAnswer){
        quantityInCart.setQuantityInCartEditionMode(editableAnswer)
    }

    QuantityInCart build(){
        return quantityInCart
    }
}

class ProductInCartBuilder{

    final DeletableAnswer yes = DeletableAnswer.yes
    final DeletableAnswer no = DeletableAnswer.no

    ProductInCart productInCart = new ProductInCart();

    def deletable(DeletableAnswer deletableAnswer){
        if (deletableAnswer == yes) productInCart.enableDeleteable()
    }

    def Poster(@DelegatesTo(strategy=Closure.DELEGATE_FIRST, value=PosterBuilder) Closure closure) {
        var layoutBuilder =  new PosterBuilder()
        def code = closure.rehydrate(layoutBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        productInCart.componentList.add(layoutBuilder.build())
    }
    def MiniDescription(@DelegatesTo(strategy=Closure.DELEGATE_FIRST, value=MiniDescriptionBuilder) Closure closure) {
        var layoutBuilder =  new MiniDescriptionBuilder()
        def code = closure.rehydrate(layoutBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
    }
    def Quantity(@DelegatesTo(strategy=Closure.DELEGATE_FIRST, value=QuantityBuilder) Closure closure) {
        var layoutBuilder =  new QuantityBuilder()
        def code = closure.rehydrate(layoutBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
    }
    def total(@DelegatesTo(strategy=Closure.DELEGATE_FIRST, value=TotalBuilder) Closure closure){
        var layoutBuilder =  new TotalBuilder()
        def code = closure.rehydrate(layoutBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        productInCart.addTotalComponent();
        productInCart.componentList.add(layoutBuilder.build())
    }
    def total(String totalLabel){
        productInCart.addTotalComponent(totalLabel)
    }
    ProductInCart build(){
        return productInCart
    }
}

class ProductsInCartBuilder{
    ProductInCart productInCart = new ProductInCart()

    def Product(@DelegatesTo(strategy=Closure.DELEGATE_FIRST, value=ProductInCartBuilder) Closure closure) {
        var layoutBuilder =  new ProductInCartBuilder()
        def code = closure.rehydrate(layoutBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        productInCart = layoutBuilder.build()
    }

    ProductInCart build(){
        return productInCart
    }
}

trait TitleBuilder{
    String title;

    def title(String content){
        this.title = content
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

    def subTotal(@DelegatesTo(strategy=Closure.DELEGATE_FIRST, value=SubTotalBuilder) Closure closure){
        var subTotalBuilder =  new SubTotalBuilder()
        def code = closure.rehydrate(subTotalBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        summary.setSubTotal(subTotalBuilder.build())
    }

    def Delivery(@DelegatesTo(strategy=Closure.DELEGATE_FIRST, value=DeliveryInCartBuilder) Closure closure){
        var deliveryInCartBuilder =  new DeliveryInCartBuilder()
        def code = closure.rehydrate(deliveryInCartBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        summary.setDelivery(deliveryInCartBuilder.build())
    }

    def total(@DelegatesTo(strategy=Closure.DELEGATE_FIRST, value=TotalBuilder) Closure closure){
        var totalBuilder =  new TotalBuilder()
        def code = closure.rehydrate(totalBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        summary.setTotal(totalBuilder.build())
    }
    Summary build(){
        return summary
    }
}

class CartBuilder implements TitleBuilder{

    Cart cart = new Cart()

    def Products(@DelegatesTo(strategy=Closure.DELEGATE_FIRST, value=ProductsInCartBuilder) Closure closure){
        var productsInCartBuilderBuilder =  new ProductsInCartBuilder()
        def code = closure.rehydrate(productsInCartBuilderBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        cart.setProductInCart(productsInCartBuilderBuilder.build())
    }

    def PromoCode(@DelegatesTo(strategy=Closure.DELEGATE_FIRST, value=PromoCodeBuilder) Closure closure){
        var promoCodeBuilder =  new PromoCodeBuilder()
        def code = closure.rehydrate(promoCodeBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        cart.setPromoCode(promoCodeBuilder.build())
    }

    def Remark(@DelegatesTo(strategy=Closure.DELEGATE_FIRST, value=RemarkBuilder) Closure closure){
        var remarkBuilder =  new RemarkBuilder()
        def code = closure.rehydrate(remarkBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        cart.setRemark(remarkBuilder.build())
    }

    def Summary(@DelegatesTo(strategy=Closure.DELEGATE_FIRST, value=SummaryBuilder) Closure closure){
        var summaryBuilder =  new SummaryBuilder()
        def code = closure.rehydrate(summaryBuilder, this,this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        cart.setSummary(summaryBuilder.build())
    }

    Cart build() {
        cart.setTitle(title)
        return cart
    }

}


class UXifier extends Script {
    WebApplication webApplication = new WebApplication()

    @Override
    Object run() {
        return null
    }

    def WebApplication(@DelegatesTo(WebApplicationBuilder) Closure closure) {
        var app = new WebApplicationBuilder(webApplication)
        closure.delegate = app
        closure()

        var application = app.build()

        var applicationVisitor = new ApplicationModelVisitorVueJS()

        applicationVisitor.visit(application)

        println applicationVisitor.vueProject
        applicationVisitor.vueProject.toCode()
    }


}

trait GenericBuilder {

    List<Component> componentList = new ArrayList<>()

    def HorizontalLayout(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = HorizontalLayoutBuilder) Closure closure) {
        var layoutBuilder = new HorizontalLayoutBuilder()
        def code = closure.rehydrate(layoutBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        addComponent(layoutBuilder.buildHorizontalLayout())
    }

    def SocialMediaGroup(@DelegatesTo(SocialMediaGroupBuiler) Closure closure) {
        var socialMediaGroupBuilder = new SocialMediaGroupBuiler()
        def code = closure.rehydrate(socialMediaGroupBuilder, this, this)
//permet de définir que tous les appels de méthodes
        code.resolveStrategy = Closure.DELEGATE_FIRST
//à l'intérieur de la closure seront résolus en utilisant le delegate
        code()

        addComponent(new SocialMediaGroup(socialMediaGroupBuilder.build()))
    }


    def ActionMenuBar(@DelegatesTo(ActionMenuBarBuilder) Closure closure) {
        var builder = new ActionMenuBarBuilder()
        var code = closure.rehydrate(builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        this.componentList.add(builder.build())
    }

    def Form(@DelegatesTo(FormBuilder) Closure closure) {
        var formBuilder = new FormBuilder()
        def code = closure.rehydrate(formBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        addComponent(formBuilder.buildForm())
    }

    def Catalog(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = CatalogBuilder) Closure closure) {
        var catalogBuilder = new CatalogBuilder()
        def code = closure.rehydrate(catalogBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        this.componentList.addAll(new Catalog(catalogBuilder.build()))
    }


    def AccordionGroup(@DelegatesTo(AccordionGroupBuilder) Closure closure) {
        var accordionGroupBuilder = new AccordionGroupBuilder()
        def code = closure.rehydrate(accordionGroupBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        addComponent(accordionGroupBuilder.buildAccordionGroup())
    }

    def addComponent(Component component) {
        this.componentList.addAll(component)
    }

    List<Component> build() {
        return componentList
    }
}

class CatalogBuilder implements GenericBuilder {

    def Product(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ProductBuilder) Closure closure) {
        var productBuilder = new ProductBuilder()
        def code = closure.rehydrate(productBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        this.componentList.add(productBuilder.buildProduct())
    }

    def Filter(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FilterBuilder) Closure closure) {
        var filterBuilder = new FilterBuilder()
        def code = closure.rehydrate(filterBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        this.componentList.add(new Filter(filterBuilder.build()))
    }

}


class ProductBuilder {

    Product product = new Product()

    final PrintingType Row = PrintingType.ROW
    final PrintingType Column = PrintingType.COLUMN


    def buildProduct() {
        return this.product
    }

    def Rating(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = RatingBuilder) Closure closure) {
        var ratingBuilder = new RatingBuilder()
        def code = closure.rehydrate(ratingBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        this.product.setRating(ratingBuilder.build())
    }

    def printType(PrintingType type){
        product.printingType = type
    }

}

class RatingBuilder {

    Rating rating = new Rating()

    final RatingType Stars = RatingType.Stars
    final RatingType Bar = RatingType.Bar
    final RatingType Mark = RatingType.Mark

    def ratingType(RatingType type) {
        this.rating.ratingType = type
    }

    Rating build() {
        return this.rating
    }

}


class FilterBuilder implements GenericBuilder {

    def PriceFilter(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PriceFilterBuilder) Closure closure) {
        var priceFilterBuilder = new PriceFilterBuilder()
        def code = closure.rehydrate(priceFilterBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        this.componentList.add(priceFilterBuilder.build())
    }

    def GenericFilters(@DelegatesTo(GenericFiltersBuilder) Closure closure) {
        var genericFiltersBuilder = new GenericFiltersBuilder()
        def code = closure.rehydrate(genericFiltersBuilder, this, this)
//permet de définir que tous les appels de méthodes
        code.resolveStrategy = Closure.DELEGATE_FIRST
//à l'intérieur de la closure seront résolus en utilisant le delegate
        code()

        this.componentList.addAll(new GenericFilters(genericFiltersBuilder.build()))
    }

}

class GenericFiltersBuilder implements GenericBuilder {

    def GenericFilter(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = GenericFilterBuilder) Closure closure) {
        var genericFilterBuilder = new GenericFilterBuilder()
        def code = closure.rehydrate(genericFilterBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        this.componentList.add(genericFilterBuilder.build())
    }
}


class PriceFilterBuilder {
    PriceFilter priceFilter = new PriceFilter()

    final PriceType Range = PriceType.Range
    final PriceType Bar = PriceType.Bar

    def priceType(PriceType type) {
        this.priceFilter.priceType = type
    }

    PriceFilter build() {
        return this.priceFilter
    }
}


class GenericFilterBuilder {

    GenericFilter genericFilter = new GenericFilter()

    def targetAtributName(String name) {
        this.genericFilter.targetAtributName = name
    }

    def targetAtributType(String type) {
        this.genericFilter.targetAtributType = type
    }

    GenericFilter build() {
        return this.genericFilter
    }

}


class SocialMediaGroupBuiler implements GenericBuilder {
    def SocialMedia(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = SocialMediaBuilder) Closure closure) {

        var socialMediaBuilder = new SocialMediaBuilder()
        def code = closure.rehydrate(socialMediaBuilder, this, this)
//permet de définir que tous les appels de méthodes
        code.resolveStrategy = Closure.OWNER_FIRST
//à l'intérieur de la closure seront résolus en utilisant le delegate
        code()
        this.componentList.add(socialMediaBuilder.build())

    }
}

class SocialMediaBuilder {
    SocialMedia socialMedia = new SocialMedia()

    def type(SocialMediaType socialMediaType) {
        this.socialMedia.type = socialMediaType
    }

    def url(String urlLink) {
        this.socialMedia.url = urlLink
    }

    SocialMedia build() {
        return this.socialMedia
    }
}

class FormBuilder implements GenericBuilder {
    String _name

    def name(String name) {
        this._name = name
    }

    def FieldGroup(@DelegatesTo(FieldGroupBuilder) Closure closure) {
        var fieldGroupBuilder = new FieldGroupBuilder()
        def code = closure.rehydrate(fieldGroupBuilder, this, this)
        code.resolveStrategy = Closure.OWNER_FIRST
        code()
        this.componentList.addAll(new FieldGroup(fieldGroupBuilder.build()))
    }

    Form buildForm() {
        var form = new Form()
        form.name = this._name
        form.componentList = componentList
        return form
    }
}

class FieldGroupBuilder implements GenericBuilder {

    def TextField(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FieldBuilder) Closure closure) {
        var fieldBuilder = new FieldBuilder()
        def code = closure.rehydrate(fieldBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        fieldBuilder.type('text-field')
        this.componentList.add(fieldBuilder.build())
    }

    def CheckBox(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FieldBuilder) Closure closure) {
        var fieldBuilder = new FieldBuilder()
        def code = closure.rehydrate(fieldBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        fieldBuilder.type('checkbox')
        this.componentList.add(fieldBuilder.build())
    }

    def ComboBox(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FieldBuilder) Closure closure) {
        var fieldBuilder = new FieldBuilder()
        def code = closure.rehydrate(fieldBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        fieldBuilder.type('combo-box')
        this.componentList.add(fieldBuilder.build())
    }

    def EmailField(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FieldBuilder) Closure closure) {
        var fieldBuilder = new FieldBuilder()
        def code = closure.rehydrate(fieldBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        fieldBuilder.type('email-field')
        this.componentList.add(fieldBuilder.build())
    }
    def RadioButtonGroup(@DelegatesTo(RadioGroupBuilder) Closure closure) {
        var radioGroupBuilder = new RadioGroupBuilder()
        def code = closure.rehydrate(radioGroupBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()

        this.componentList.add(radioGroupBuilder.buildRadioGroup())

        /*
        */
    }

    def DatePicker(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FieldBuilder) Closure closure) {
        var fieldBuilder = new FieldBuilder()
        def code = closure.rehydrate(fieldBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        fieldBuilder.type('date-picker')
        this.componentList.add(fieldBuilder.build())
    }

    def DateTimePicker(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FieldBuilder) Closure closure) {
        var fieldBuilder = new FieldBuilder()
        def code = closure.rehydrate(fieldBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        fieldBuilder.type('date-time-picker')
        this.componentList.add(fieldBuilder.build())
    }

    def Button(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FieldBuilder) Closure closure) {
        var fieldBuilder = new FieldBuilder()
        def code = closure.rehydrate(fieldBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        fieldBuilder.type('button')
        this.componentList.add(fieldBuilder.build())
    }

    def PasswordField(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FieldBuilder) Closure closure) {
        var fieldBuilder = new FieldBuilder()
        def code = closure.rehydrate(fieldBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        fieldBuilder.type('password-field')
        this.componentList.add(fieldBuilder.build())
    }

    def RichText(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FieldBuilder) Closure closure) {
        var fieldBuilder = new FieldBuilder()
        def code = closure.rehydrate(fieldBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        fieldBuilder.type('rich-text-editor')
        this.componentList.add(fieldBuilder.build())
    }

    def TimePicker(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FieldBuilder) Closure closure) {
        var fieldBuilder = new FieldBuilder()
        def code = closure.rehydrate(fieldBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        fieldBuilder.type('time-picker')
        this.componentList.add(fieldBuilder.build())
    }

    def Upload(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FieldBuilder) Closure closure) {
        var fieldBuilder = new FieldBuilder()
        def code = closure.rehydrate(fieldBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        fieldBuilder.type('upload')
        this.componentList.add(fieldBuilder.build())
    }
}

class FieldBuilder {
    Field field = new Field();

    def name(String name) {
        this.field.name = name;
    }

    def type(String type) {
        this.field.type = type;
    }

    Field build() {
        return this.field;
    }
}

class RadioGroupBuilder implements GenericBuilder{
    RadioGroup radioGroup = new RadioGroup();

    def name(String name) {
        this.radioGroup.name = name;
    }

    def RadioButton(@DelegatesTo(RadioButtonBuilder) Closure closure) {
        var radioButtonBuilder = new RadioButtonBuilder()
        def code = closure.rehydrate(radioButtonBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        this.radioGroup.componentList.add(radioButtonBuilder.buildRadioButton())
    }

    RadioGroup buildRadioGroup(){
        return this.radioGroup;
    }
}

class RadioButtonBuilder implements GenericBuilder{
    Field field = new Field();

    def name(String name) {
        this.field.name = name;
    }

    def type(String type) {
        this.field.type = type;
    }

    Field buildRadioButton() {
        this.field.type = "radio-button";
        return this.field;
    }
}

class AccordionGroupBuilder implements GenericBuilder {
    AccordionGroup accordionGroup = new AccordionGroup();

    def Accordion(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = AccordionBuilder) Closure closure) {
        var accordionBuilder = new AccordionBuilder()
        def code = closure.rehydrate(accordionBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        this.accordionGroup.componentList.add(accordionBuilder.buildAccordion())
    }

    AccordionGroup buildAccordionGroup() {
        return accordionGroup;
    }

}

class AccordionBuilder implements GenericBuilder {
    Accordion accordion = new Accordion()

    def name(String name) {
        this.accordion.name = name;
    }

    Accordion buildAccordion(){
        return accordion;
    }

    def addComponent(Component component) {
        this.accordion.componentList.addAll(component)
    }

    @Override
    public String toString() {
        return "AccordionBuilder{" +
                "accordion=" + accordion +
                '}';
    }
}

class HorizontalLayoutBuilder implements GenericBuilder {
    HorizontalLayout horizontalLayout = new HorizontalLayout();

    HorizontalLayout buildHorizontalLayout(){
        return horizontalLayout;
    }

    def TextField(@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FieldBuilder) Closure closure) {
        var fieldBuilder = new FieldBuilder()
        def code = closure.rehydrate(fieldBuilder, this, this)
        code.resolveStrategy = Closure.DELEGATE_FIRST
        code()
        fieldBuilder.type('text-field')
        this.componentList.add(fieldBuilder.build())
    }

    def addComponent(Component component) {
        this.horizontalLayout.componentList.addAll(component)
    }
}
