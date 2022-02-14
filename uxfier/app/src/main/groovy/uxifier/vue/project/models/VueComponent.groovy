package uxifier.vue.project.models

import com.fasterxml.jackson.core.type.TypeReference
import groovy.transform.ToString

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
        content.forEach(c -> c.insertInTemplate())
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
    def insertInTemplate() {
        FileContext.writer.write("""
 <div>
        <div>
            Mon Panier
            <hr/>
        </div>
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
    </div>
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
    def writeScript() {
        return null
    }

    @Override
    def insertSelfInImports() {
        return null
    }
}

trait VueGeneratable {

    //NOTE : deux contextes pour un composant un où on génère son propre fichier et un où il est utilisé dans un autre fichier


    abstract def registerDependencies(PackageJson packageJson)

    /**
     * Write own template
     */
    def writeTemplate(){

    }

    abstract def writeScript()

    def registerSelfInComponents(){

    }

    abstract def insertSelfInImports()

    /**
     * insert self in a parent template
     * @return
     */
    abstract def insertInTemplate()

    def toCode(VueProject project) {
        this.registerDependencies(project.packageJson)
        this.writeTemplate()
        this.writeScript()
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
