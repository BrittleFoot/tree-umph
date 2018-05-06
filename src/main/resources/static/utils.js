utils_js = true


function fading_value(n, initial, updater) {
    fv = {
        ttl: n,
        value: initial,
        ttl_init: n,
        updater: updater
    }

    fv.updater(fv)

    return fv
}

function get_fading(fv) {
    fv.ttl = fv.ttl - 1
    if (fv.ttl < 0) {
        updater(fv)
        fv.ttl = fv.ttl_init
    }
    return fv.value
}


n = 100

languages = fading_value(n, [], function(fv) {
    http_get('/api/languages', function() {
        if (this.readyState == 4 && this.status == 200) {
            fv.value = JSON.parse(this.responseText)
        }
    })
})

bindings = fading_value(n, [], function(fv) {
    http_get('/api/bindings', function() {
        if (this.readyState == 4 && this.status == 200) {
            fv.value = JSON.parse(this.responseText)
        }
    })
})

types = fading_value(n, [], function(fv) {
    http_get('/api/function_types', function() {
        if (this.readyState == 4 && this.status == 200) {
            fv.value = JSON.parse(this.responseText)
        }
    })
})


mode = {
    js: 'javascript',
    python: 'python',
    groovy: 'groovy',
    ECMAScript: 'javascript',
    Groovy: 'groovy'
}


function ga_languages() {
    return get_fading(languages)
}


function ga_types() {
    return get_fading(types)
}


function ga_bindings() {
    return get_fading(bindings)
}


function create_div(clazz) {
    var div = document.createElement('div')
    div.setAttribute('class', clazz)
    return div
}


function wrap(with_tag, what) {
    var res = document.createElement(with_tag)
    res.appendChild(what)
    return res
}


function tag_with_text(tagname, text) {
    var tag = document.createElement(tagname)
    tag.innerHTML = text
    return tag
}


function LabeledInput(clazz, initial_value) {
    var elem = create_div(clazz)
    var label = tag_with_text('a', clazz + ": ")
    var input = document.createElement('input')
    input.classList.add('input')

    elem.appendChild(label)
    elem.appendChild(input)
    input.setAttribute('value', initial_value)

    elem.getValue = function() { return input.value }

    return elem
}


function LabeledSelector(clazz, options, selected) {
    var elem = create_div(clazz)

    var label = tag_with_text('a', clazz + ": ")

    var select = document.createElement('select')
    select.classList.add('select')
    select.classList.add(clazz)

    for (var i = 0; i < options.length; i++) {
        var opt_text = options[i]
        var option = tag_with_text('option', opt_text)
        option.classList.add('select')
        option.classList.add('option')
        if (selected == opt_text) {
            option.setAttribute('selected', 'selected')
        }
        select.appendChild(option)
    }

    elem.appendChild(label)
    elem.appendChild(select)

    elem.getValue = function() {
        return select.options[select.selectedIndex].label
    }

    elem.select = select
    elem.select.onfocus = function() {
        elem.select.previous = elem.getValue()
    }
    return elem
}


function remove_from_parent(node) {
    node.parentElement.removeChild(node)
}


function _hashf(a,b) {
    a = ((a << 5) - a) + b.charCodeAt(0)
    return a & a
}

hashCode = function(s){
  return Math.abs(s.split("").reduce(_hashf,0))
}


function http_get(url, andThen) {
    var request = new XMLHttpRequest()
    request.onreadystatechange = andThen
    request.open("GET", url, true)
    request.send()
}

function http_post(url, payload_obj, andThen) {
    var request = new XMLHttpRequest()

    request.onreadystatechange = andThen
    request.open("POST", url, true)
    request.setRequestHeader('Content-type', 'application/json; charset=utf-8');
    request.send(JSON.stringify(payload_obj))
}

function download(data, filename, type) {
    var file = new Blob([data], {type: type});
    if (window.navigator.msSaveOrOpenBlob) // IE10+
        window.navigator.msSaveOrOpenBlob(file, filename);
    else { // Others
        var a = document.createElement("a"),
                url = URL.createObjectURL(file);
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        setTimeout(function() {
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        }, 0);
    }
}
