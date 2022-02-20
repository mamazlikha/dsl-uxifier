package uxifier.vue.project.models

import com.fasterxml.jackson.core.type.TypeReference
import groovy.transform.ToString
import uxifier.models.Cart
import uxifier.models.Component
import uxifier.models.DeliveryInCart
import uxifier.models.MiniDescription
import uxifier.models.Poster
import uxifier.models.ProductInCart
import uxifier.models.PromoCode
import uxifier.models.QuantityInCart
import uxifier.models.Remark
import uxifier.models.SubTotal
import uxifier.models.Summary
import uxifier.models.Total

import java.nio.file.Files
import java.nio.file.Path

class VueComponent implements VueGeneratable {
    VueTemplateElement template;
    ScriptElement script;
    String name
    List<VueGeneratable> content = new ArrayList<>();

    @Override
    def addContent(VueGeneratable vueGeneratable) {
        this.content.add(vueGeneratable)
    }

    @Override
    def registerDependencies(PackageJson packageJson) {
        return null
    }

    @Override
    def writeTemplate() {
        println("generating vuecomponent with name  ${name} ")
        var componentFilePath = Files.createFile(Path.of(FileContext.currentDirectory.toString(), this.name + '.vue'))

        FileContext.writer = Files.newBufferedWriter(componentFilePath)
        FileContext.writer.write("<template>")
        println("wrote <template> ${name}")
        content.forEach(c -> c.openTagInTemplate())
        content.forEach(c -> c.insertInTemplate())
        content.forEach(c -> c.closeTagInTemplate())
        println("wrote inserted content ${name}")
        FileContext.writer.write("</template>")

    }

    @Override
    def insertSelfInImports() {
        FileContext.writer.write("import ${this.name} from './components/${this.name}.vue'\n")
    }

    @Override
    def registerSelfInComponents() {
        FileContext.writer.write("${this.name},")

    }

    @Override
    def writeScript() {
        println 'importing libraries for all components...'
        FileContext.writer.write("<script>")
        content.forEach(c -> c.insertSelfInImports())
        FileContext.writer.write("</script>")

        FileContext.writer.close()
        FileContext.writer = null


    }

    @Override
    def insertInTemplate() {
        FileContext.writer.write("<${this.name}/>")
    }

    @Override
    def openTagInTemplate() {
        return null
    }

    @Override
    def closeTagInTemplate() {
        return null
    }

    @Override
    String toString() {
        return "VueComponent {name = ${name} } -> ${content}"
    }
}

class VueTemplateElement {
    List<HtmlElement> elements = new ArrayList<>()
}

class ScriptElement {

}

trait HtmlElement {

}

trait LeafHtmlElement implements HtmlElement {

}

trait CompositeHtmlElement implements HtmlElement {
    List<HtmlElement> elements = new ArrayList<>()
}

class HorizontalLayout implements HtmlElement {

}

class VerticalLayout implements HtmlElement {

}

class VueJsSocialMediaGroup implements VueGeneratable {
    List<VueGeneratable> socialMedia = new ArrayList<>();

    @Override
    String toString() {
        return "VueJsSocialMediaGroup -> ${socialMedia}"
    }

    @Override
    def addContent(VueGeneratable vueGeneratable) {
        return socialMedia.add(vueGeneratable)
    }

    @Override
    def registerDependencies(PackageJson packageJson) {
        return null;
    }

    @Override
    def writeTemplate() {

    }

    @Override
    def writeScript() {
        return null
    }

    @Override
    def insertSelfInImports() {
        return null
    }

    @Override
    def insertInTemplate() {
        FileContext.writer.write(
                """
<div><link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.13.0/css/all.min.css" integrity="sha256-h20CPZ0QyXlBuAw7A+KluUYx/3pK+c7lYEpqLTlxjYQ=" crossorigin="anonymous" />
                    """)
        socialMedia.forEach(s -> s.insertInTemplate())
        FileContext.writer.write("</div>")

    }

}

class VueJsCart implements VueGeneratable {
    Cart cart;

    List<VueGeneratable> cartContent = new ArrayList<>();

    public VueJsCart(Cart cart){
        this.cart = cart
    }

    @Override
    def addContent(VueGeneratable vueGeneratable) {
        return cartContent.add(vueGeneratable)
    }

    @Override
    def registerDependencies(PackageJson packageJson) {
        return null
    }

    @Override
    def insertSelfInImports() {
        return null
    }

    @Override
    def writeScript() {
        return null
    }

    @Override
    def openTagInTemplate() {
        return null
    }

    @Override
    def closeTagInTemplate() {
        return null
    }
  
    def insertInTemplate() {
        FileContext.writer.write("""
 <div>
        <div>"""
                +
                cart.getTitle()
                +
            """
            <hr/>"""
                +
        """</div>
        """)
        cartContent.forEach(c->c.insertInTemplate())
        cart.productInCart.FileContext.writer.write("""           
    </div>
        """)
    }
}

class VueJsProductInCart implements VueGeneratable {
    ProductInCart productInCart;

    List<VueGeneratable> productInCartContent = new ArrayList<>();

    public VueJsProductInCart(ProductInCart productInCart){
        this.productInCart = productInCart
    }

    @Override
    def insertSelfInImports() {
        return null
    }

    @Override
    def writeScript() {
        return null
    }

    def insertInTemplate() {
        FileContext.writer.write("""
         <div class="horizontal-container" style="display:flex;">
          <div class='actual-map'>
            <img id="myimage" src="https://picsum.photos/200" width="200">
          </div>
          <div class="vertical_separator" style="border-left: 6px solid transparent;height: max;"></div>
          <div id="product_in_cart_description" style="flex-grow:1;">
              <h3 id="product_1_name">Je suis un article</h3>
              <h4 id="product_1_price">400<span class="devise">£</span></h4>
          </div>
          <div class="vertical_separator" style="border-left: 6px solid transparent;height: max;width:40%;"></div>
          <div style="display:flex;flex-grow:1;">
              <div><input id="product_1_quantity" type="number" height="10px"/></div>
              <div class="vertical_separator" style="border-left: 6px solid transparent;height: max; width:20%;"></div>
              <div style="flex-grow:1;"><span id="product_1_total">400</span><span class="devise">£</span></div>
              <div class="vertical_separator" style="border-left: 6px solid transparent;height: max;width:20%;"></div>
              <div><button class="btn"><i class="fa fa-close"></i></button></div>
          </div>
      
        </div>
        """)
    }
}

class VueJsPromoCode implements VueGeneratable {

    PromoCode promoCode;

    List<VueGeneratable> promoCodeContent = new ArrayList<>();

    public VueJsPromoCode(PromoCode promoCode){
        this.promoCode = promoCode
    }

    @Override
    def insertSelfInImports() {
        return null
    }

    @Override
    def writeScript() {
        return null
    }

    def insertInTemplate() {
        FileContext.writer.write("""
 VueJsPromoCode
        """)
    }
}

class VueJsRemark implements VueGeneratable {
    Remark remark;

    List<VueGeneratable> remarkContent = new ArrayList<>();

    public VueJsRemark(Remark remark){
        this.remark = remark
    }

    @Override
    def insertSelfInImports() {
        return null
    }

    @Override
    def writeScript() {
        return null
    }

    def insertInTemplate() {
        FileContext.writer.write("""
 VueJsRemark
        """)
    }
}

class VueJsSummary implements VueGeneratable {
    Summary summary;

    List<VueGeneratable> summaryContent = new ArrayList<>();

    public VueJsSummary(Summary summary){
        this.summary = summary
    }

    @Override
    def addContent(VueGeneratable vueGeneratable) {
        return summaryContent.add(vueGeneratable)
    }

    @Override
    def insertSelfInImports() {
        return null
    }

    @Override
    def writeScript() {
        return null
    }

    def insertInTemplate() {
        FileContext.writer.write("""
 VueJsSummary
        """)
    }
}

class VueJsPoster implements VueGeneratable {
    Poster poster;

    public VueJsPoster(Poster poster){
        this.poster = poster
    }

    @Override
    def insertSelfInImports() {
        return null
    }

    @Override
    def writeScript() {
        return null
    }

    def insertInTemplate() {
        FileContext.writer.write("""
 VueJsPoster
        """)
    }
}

class VueJsMiniDescription implements VueGeneratable {
    MiniDescription miniDescription;

    public VueJsMiniDescription(MiniDescription miniDescription){
        this.miniDescription = miniDescription
    }

    @Override
    def writeScript() {
        return null
    }

    @Override
    def insertSelfInImports() {
        return null
    }

    def insertInTemplate() {
        FileContext.writer.write("""
VueJsMiniDescription
        """)
    }
}

class VueJsQuantityOfProductInCart implements VueGeneratable {
    QuantityInCart quantityInCart;

    public VueJsQuantityOfProductInCart(QuantityInCart quantityInCart){
        this.quantityInCart = quantityInCart
    }

    @Override
    def writeScript() {
        return null
    }

    @Override
    def insertSelfInImports() {
        return null
    }

    def insertInTemplate() {
        FileContext.writer.write("""
VueJsQuantityOfProductInCart
        """)
    }
}

class VueJsTotal implements VueGeneratable {
    Total total;

    public VueJsTotal(Total total){
        this.total = total
    }

    @Override
    def writeScript() {
        return null
    }

    @Override
    def insertSelfInImports() {
        return null
    }

    def insertInTemplate() {
        FileContext.writer.write("""
VueJsTotal
        """)
    }
}

class VueJsSubTotal implements VueGeneratable {
    SubTotal subTotal;

    public VueJsSubTotal(SubTotal subTotal){
        this.subTotal = subTotal
    }

    @Override
    def writeScript() {
        return null
    }

    @Override
    def insertSelfInImports() {
        return null
    }

    def insertInTemplate() {
        FileContext.writer.write("""
VueJsSubTotal
        """)
    }
}
class VueJsDeliveryInCart implements VueGeneratable {
    DeliveryInCart deliveryInCart;

    public VueJsDeliveryInCart(DeliveryInCart deliveryInCart){
        this.deliveryInCart = deliveryInCart
    }

    @Override
    def writeScript() {
        return null
    }

    @Override
    def insertSelfInImports() {
        return null
    }

    def insertInTemplate() {
        FileContext.writer.write("""
VueJsDeliveryICart
        """)
    }
}

class VueJsSocialMedia implements VueGeneratable {

    private final static Map<String, SocialMediaIconInfo> iconMaps = new HashMap<>();

    private final String name
    private final String link;

    VueJsSocialMedia(String name, String link) {
        this.name = name
        this.link = link
    }

    static {
         List<SocialMediaIconInfo>  iconInfos = FileContext.objectMapper.readValue(new String(VueJsSocialMedia.getResourceAsStream("/social-media.json").readAllBytes()),new TypeReference<List<SocialMediaIconInfo>>() {} )

        iconInfos.forEach( info -> iconMaps.put(info.name, info))

    }

    @Override
    String toString() {
        return "VueJsSocialMedia {name = ${name} , link = ${link} }"
    }

    @Override
    def registerDependencies(PackageJson packageJson) {
        return null
    }

    @Override
    def insertInTemplate() {
        println("in vuejs-socialmedia ${this.name}")
        FileContext.writer.write("""\n<a href="${this.link}"> <em style="color:${iconMaps.get(this.name).color};" class="${iconMaps.get(this.name).icon}"> </em></a>""")
    }

    @Override
    def openTagInTemplate() {
        return null
    }

    @Override
    def closeTagInTemplate() {
        return null
    }

    @Override
    def writeScript() {
        return null
    }

    @Override
    def insertSelfInImports() {
        return null
    }
}

class VueJsForm implements VueGeneratable{

    String name
    List<VueGeneratable> fields = new ArrayList<>();

    @Override
    def registerDependencies(PackageJson packageJson) {
        return null
    }

    @Override
    def writeScript() {
        return null
    }

    @Override
    def insertSelfInImports() {
        FileContext.writer.write("""
            import '@vaadin/text-field';
            import '@vaadin/checkbox';
            import '@vaadin/combo-box';
            import '@vaadin/email-field';
            import '@vaadin/date-picker';
            import '@vaadin/date-time-picker';
            import '@vaadin/button';
            import '@vaadin/message-input';
            import '@vaadin/password-field';
            import '@vaadin/time-picker';
            import '@vaadin/upload';
            
            import '@vaadin/radio-group';
            
            export default {
            
            }
        """)
    }

    @Override
    def insertInTemplate() {
        println 'creating form : ' + name + 'with fields size' + fields.size()
        FileContext.writer.write("""<div class=${name}>""")

        fields.forEach(s -> s.insertInTemplate())

        FileContext.writer.write("""</div>""")
    }

    @Override
    def openTagInTemplate() {
        return null
    }

    @Override
    def closeTagInTemplate() {
        return null
    }


    @Override
    public String toString() {
        return "VueJsForm{" +
                "name='" + name + '\'' +
                ", fields=" + fields +
                '}';
    }
}

class VueJsField implements VueGeneratable{

    String name
    String type

    @Override
    def registerDependencies(PackageJson packageJson) {
        return null
    }

    @Override
    def writeScript() {
        return null
    }

    @Override
    def insertSelfInImports() {
        return null
    }

    @Override
    def insertInTemplate() {
        FileContext.writer.write("""<vaadin-${type} label="${name}"/><br/>""")
    }

    @Override
    def openTagInTemplate() {
        return null
    }

    @Override
    def closeTagInTemplate() {
        return null
    }
}

class VueJsAccordionGroup implements VueGeneratable{

    List<VueGeneratable> accordions = new ArrayList<>();

    @Override
    def writeScript() {
        return null
    }

    @Override
    def insertSelfInImports() {
        println "importing libraries for Accorions"
        FileContext.writer.write("""

            import '@vaadin/accordion';
            import '@vaadin/vertical-layout';
            
            export default {
            
            }
        """)
    }

    @Override
    def insertInTemplate() {
        for(VueGeneratable v : accordions){
            v.insertSelfInImports()
            v.openTagInTemplate()
            v.insertInTemplate()
            v.closeTagInTemplate()
        }
    }

    @Override
    def openTagInTemplate() {
        FileContext.writer.write("""<vaadin-accordion style="width:50%; margin-left: 2%">""")
    }

    @Override
    def closeTagInTemplate() {
        FileContext.writer.write("""</vaadin-accordion>""")
    }


    @Override
    public String toString() {
        return "VueJsAccordionGroup{" +
                "accordions=" + accordions +
                '}';
    }
}

class VueJsAccordion implements VueGeneratable{
    String name
    List<VueGeneratable> components = new ArrayList<>()

    @Override
    def writeScript() {
        return null
    }

    @Override
    def insertSelfInImports() {
        return null
    }

    @Override
    def insertInTemplate() {
        for(VueGeneratable v : components){
            v.insertInTemplate()
        }
    }

    @Override
    def openTagInTemplate() {
        FileContext.writer.write("""<vaadin-accordion-panel>
        <vaadin-vertical-layout>""")
    }

    @Override
    def closeTagInTemplate() {
        FileContext.writer.write("""</vaadin-vertical-layout>
        </vaadin-accordion-panel>""")
    }


    @Override
    public String toString() {
        return "VueJsAccordion{" +
                "name='" + name + '\'' +
                ", components=" + components +
                '}';
    }
}


trait VueGeneratable {

    //NOTE : deux contextes pour un composant un où on génère son propre fichier et un où il est utilisé dans un autre fichier


    def registerDependencies(PackageJson packageJson){

    }

    /**
     * Write own template
     */
    def writeTemplate() {

    }

    abstract def writeScript()

    def registerSelfInComponents() {

    }

    def writeStyle() {

    }

    def insertSelfInStyle() {

    }

    abstract def insertSelfInImports()

    /**
     * insert self in a parent template
     * @return
     */
    abstract def insertInTemplate()


        
    /**
     * used for nested components
     * such as tabs or accordions
     * */
    def openTagInTemplate(){

    }

   def closeTagInTemplate(){

    }

    def toCode(VueProject project) {
        this.registerDependencies(project.packageJson)
        this.writeTemplate()
        this.writeScript()
        this.writeStyle()
        if(FileContext.writer != null && FileContext.writer){
            FileContext.writer.close()
        }
            
    }

    def addContent(VueGeneratable vueGeneratable) {

    }
}

@ToString
class SocialMediaIconInfo {
    String network
    String name
    String icon
    String color
}

