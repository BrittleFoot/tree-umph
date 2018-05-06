utils_js


function_element_js = true


function_element = []

function_element['ScriptedByteFunction'] =  {
    reader: scripted_function_reader,
    writer: scripted_function_writer,
    template: {
        language: 'Groovy',
        text: "def apply(byte[] bytes) {\n    bytes\n}",
        bindings: []
    }
}

function_element['ScriptedByteSupplier'] =  {
    reader: scripted_function_reader,
    writer: scripted_function_writer,
    template: {
        language: 'Groovy',
        text: "def get() {\n    [42] as byte[]\n}",
        bindings: []
}
}

function_element['ScriptedBytePredicate'] = {
    reader: scripted_function_reader,
    writer: scripted_function_writer,
    template: {
        language: 'Groovy',
        text: "def test(byte[] bytes) {\n    true\n}",
        bindings: []
    }
}

function_element['ByteSupplier'] = {
    reader: byte_supplier_reader,
    writer: byte_supplier_writer,
    template: { value: "" }
}


function get_remove_binding(bindings, binding , btn, root) {
    return function() {
        bindings.removeChild(binding)
        renew_options(btn, root.getBindings())
    }
}



function renew_options(select, selected) {

    for (var i = 0; i < select.childNodes.length; i++) {
        var opt = select.childNodes[i]
        opt.style.display = selected.indexOf(opt.label) == -1 ? 'block' : 'none'
    }

}


function build_bindings_selector(a_bindings) {
    var plus = '&nbsp;+&nbsp;'

    var bindings = create_div('bindings')
    bindings.appendChild(tag_with_text('a', 'bindings: '))

    var btn = document.createElement('select')


    var b_list = create_div('inline')
    for (var i = 0; i < a_bindings.length; i++) {
        var binding = tag_with_text('a', a_bindings[i])

        binding.classList.add('removable')
        binding.classList.add('list_item')
        binding.onclick = get_remove_binding(
            b_list, 
            binding, 
            btn, 
            bindings
        )

        b_list.appendChild(binding)
    }

    var opt = tag_with_text('option', plus)
    btn.setAttribute('class', 'select')
    opt.setAttribute('class', 'option')
    opt.setAttribute('selected', 'selected')

    var b = ga_bindings()

    btn.appendChild(opt)
    for (var i = 0; i < b.length; i++) {
        btn.appendChild(tag_with_text('option', b[i]))
    }

    btn.onchange = function() {
        if (btn.selectedIndex == 0)
            return

        var selected_binding = btn.options[btn.selectedIndex].label
        var binding = tag_with_text('a', selected_binding)
        binding.classList.add('removable')
        binding.classList.add('list_item')
        binding.onclick = get_remove_binding(
            b_list, 
            binding, 
            btn, 
            bindings
        )
        b_list.appendChild(binding)
        
        btn.selectedIndex = 0
        renew_options(btn, bindings.getBindings())
    }

    bindings.appendChild(b_list)
    bindings.appendChild(btn)

    bindings.getBindings = function() {
        var res = []
        for (var i = 0; i < b_list.childNodes.length; i++) {
            res.push(b_list.childNodes[i].text)
        }
        return res
    }

    renew_options(btn, bindings.getBindings())
    return bindings
}


function scripted_function_reader(obj) {
    var code = create_div('function')

    var lang = LabeledSelector('language', ga_languages(), obj.language)
    code.appendChild(lang)

    var bindings = build_bindings_selector(obj.bindings)

    code.appendChild(bindings)
    code.appendChild(document.createElement('br'))

    var codemirror = CodeMirror(code, {
        value: obj.text,
        mode: mode[obj.language],
        lineNumbers: true,
        viewportMargin: Infinity,
        dragDrop: true,
        autoCloseBrackets: true,
        matchBrackets: true,
        tabSize: 4,
        indentUnit: 4
    });

    if (obj.stdout) {
        code.appendChild(tag_with_text('a', 'Script output')).style.margin = '0.1em'
        var cm_out = CodeMirror(code, {
            readOnly: true,
            value: obj.stdout.trim(),
            lineNumbers: true,
            viewportMargin: Infinity,
            dragDrop: false,
            tabSize: 4,
            indentUnit: 4
        });

        setTimeout(function() { cm_out.refresh() }, 1)
    }

    if (obj.stderr) {
        code.appendChild(tag_with_text('a', 'Runtime error')).style.margin = '0.1em'
        var cm_err = CodeMirror(code, {
            readOnly: true,
            value: obj.stderr.trim(),
            lineNumbers: true,
            viewportMargin: Infinity,
            dragDrop: false,
            tabSize: 4,
            indentUnit: 4
        });

        setTimeout(function() { code.classList.add("error_appears") }, 10)
        setTimeout(function() { cm_err.refresh() }, 1);
    }

    if (obj.error) {
        code.appendChild(tag_with_text('a', 'Compilation error')).style.margin = '0.1em'
        var cm_err = CodeMirror(code, {
            readOnly: true,
            value: obj.error.trim(),
            lineNumbers: true,
            viewportMargin: Infinity,
            dragDrop: false,
            tabSize: 4,
            indentUnit: 4
        });

        setTimeout(function() { code.classList.add("error_appears") }, 10)
        setTimeout(function() { cm_err.refresh() }, 1);
    }



    codemirror.setOption("extraKeys", {
      Tab: function(cm) {
        var iu = cm.getOption("indentUnit")
        var spaces = Array(iu + 1).join(" ");
        cm.replaceSelection(spaces);
      }
    });

    setTimeout(function() { codemirror.refresh() }, 1);

    code.getText = function() { return codemirror.getValue() }
    code.getLanguage = lang.getValue
    code.getBindings = bindings.getBindings

    lang.select.onchange = function() {
        setTimeout(function() {
            var label = lang.select.options[lang.select.selectedIndex].label
            codemirror.setOption("mode", mode[label])
            codemirror.refresh()
        }, 1)
    }

    return code
}


function scripted_function_writer(node) {
    var func = {}
    func.text = node.getText()
    func.language = node.getLanguage()
    func.bindings = node.getBindings()
    return func
}


function byte_supplier_reader(obj) {

    var supplier = create_div('bytesupplier')

    supplier.appendChild(document.createElement('br'))

    var greet = "Click here to choose file. "

    if (obj.value) {
        greet = "< data #" + hashCode(obj.value) +  
                " > Click here to override file. "
    }


    var label = supplier.appendChild(document.createElement('label')),
        text = label.appendChild(tag_with_text('a', greet))
        input = label.appendChild(document.createElement('input'))

    label.setAttribute('class', 'fileContainer')
    input.setAttribute('type', 'file')

    var btn_load = document.createElement('input')
    btn_load.setAttribute('type', 'button')
    btn_load.setAttribute('value', 'load')

    input.onchange = function() {
        if (input.files && input.files[0])
            text.innerHTML = input.files[0].name + ' '
    }

    btn_load.onclick = function() {

        if (!input || !input.files || !input.files[0]) {
            console.log('invalid input')
            console.log(input)
            return
        }

        var fr = new FileReader();
        fr.onload = function(e) {
            supplier.isReady = function() { return true }
            supplier.getValue = function() { 
                return btoa(e.target.result) 
            }
            text.innerHTML =  "< data #" + hashCode(supplier.getValue()) +  
                " > (" + input.files[0].name + 
                ") Click here to override file. "
        }
        fr.readAsBinaryString(input.files[0])
    }

    supplier.appendChild(btn_load)
    supplier.appendChild(document.createElement('br'))
    supplier.appendChild(document.createElement('br'))

    supplier.getValue = function() { return obj.value }
    supplier.isReady = function() { return false }

    return supplier
}


function byte_supplier_writer(node) {
    var func = {}
    func.value = node.getValue()
    return func
}



function create_function_element(obj) {
    var type = obj.type


    var creator = function_element[type]
    if (creator === undefined) {
        return tag_with_text('p', '&lt;built-in function ' + type + '&gt;')
    }

    return creator.reader(Object.assign(Object.assign({}, creator.template), obj))
}


function store_function(type, func_node) {
    var creator = function_element[type]
    if (!creator) {
        return { type: type }
    }

    var func = creator.writer(func_node)
    func.type = type
    return Object.assign(Object.assign({}, creator.template), func)
}
