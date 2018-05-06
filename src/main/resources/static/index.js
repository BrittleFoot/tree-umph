utils_js
function_element_js



function cb_get_add_stage(element, clazz) {
    return function() {

        template = {
            name: 'unnamed',
            status: 'PENDING',
            result: null,
            function: {
                type: "None"
            },
            requiredStages: [],
            optionalStages: []
        }

        console.log(element)

        var stage = create_stage_node(template)
        if (clazz) stage.classList.add(clazz)
        element.appendChild(stage)
        element.style.display = 'block'
    }
}


function cb_get_replace_function(current_function_layout) {
    var fl = current_function_layout
    return function(e) {
        var prev_func = fl.firstChild
        var func = {}

        if (fl.firstChild) {
            func = store_function(e.target.previous, prev_func)
            fl.removeChild(fl.firstChild)
        } else {
            prev_func = {}
        }


        var new_ftype = e.target.options[e.target.selectedIndex].label

        func.type = new_ftype
        fl.appendChild(create_function_element(func))     

        e.target.previous = new_ftype
   }
}


function create_stage_node(obj) {
    var node = create_div('stage_node'),
        content = create_div('content'),
        required = create_div('required_layout'),
        optional = create_div('optional_layout'),
        remove = create_div('remove')


    remove.classList.add('button')
    remove.appendChild(tag_with_text('a', '&nbsp;x&nbsp;'))
    remove.onclick = function() { remove_from_parent(node) }
    
    node.appendChild(remove)
    node.appendChild(content)

    var name = LabeledInput('name', obj.name),
        type = LabeledSelector('type', ga_types(), obj.function.type),
        func = create_function_element(obj.function),
        stat = create_div('status'),
        func_layout = create_div('func_layout')

    stat.appendChild(tag_with_text('a', obj.status))
    if (obj.status == 'COMPLETED') {
        var res = obj.result + ''

        if (res.length > 50) {
            res = res.substr(0, 50) + '...'
        }

        stat.appendChild(tag_with_text('a', ': '))
        stat.appendChild(tag_with_text('a', res))
    }

    var status_cpy = obj.status
    var result_cpy = obj.result
    stat.getValue = function() { return status_cpy }
    stat.getResult = function() { return result_cpy }

    type.select.onchange = cb_get_replace_function(func_layout)

    func_layout.appendChild(func)

    content.appendChild(name)
    content.appendChild(type)
    content.appendChild(func_layout)
    content.appendChild(stat)

    node.appendChild(required)
    if (obj.requiredStages.length == 0) {
        required.style.display = 'none'
    }
    
    for (var i = 0; i < obj.requiredStages.length; i++) {
        var tstage = create_stage_node(obj.requiredStages[i])
        tstage.classList.add('required')
        required.appendChild(tstage)
    }

    var b_add = create_div('button')
    b_add.classList.add('add')
    b_add.classList.add('required')
    b_add.appendChild(tag_with_text('center', '+'))
    b_add.onclick = cb_get_add_stage(required, 'required')
    node.appendChild(b_add)
    
    node.appendChild(optional)
    if (obj.optionalStages.length == 0) {
        optional.style.display = 'none'
    }

    for (var i = 0; i < obj.optionalStages.length; i++) {
        var tstage = create_stage_node(obj.optionalStages[i])
        tstage.classList.add('optional')
        optional.appendChild(tstage)
    }

    var b_add = create_div('button')
    b_add.classList.add('optional')
    b_add.classList.add('add')
    b_add.appendChild(tag_with_text('center', '+'))
    node.appendChild(b_add)
    b_add.onclick = cb_get_add_stage(optional, 'optional')

    node.getName = name.getValue
    node.getType = type.getValue
    node.getStatus = stat.getValue
    node.getResult = stat.getResult
    node.getFunction = function() { return func_layout.firstChild }
    node.getRequiredStages = function() { return required.childNodes } 
    node.getOptionalStages = function() { return optional.childNodes } 

    return node
}


function load_process(process_node, obj) {

    while (process_node.firstChild) {
        process_node.removeChild(process_node.firstChild)
    }

    var name = LabeledInput('name', obj.name)
    process_node.appendChild(name)

    process_node.getName = name.getValue

    var stages = create_div('stages')
    process_node.appendChild(stages)

    process_node.stages = stages.childNodes

    for (var i = 0; i < obj.stages.length; i++) {
        var created = create_stage_node(obj.stages[i])
        stages.appendChild(created)
        created.classList.add('required')
    }

    var b_add = create_div('button')
    b_add.classList.add('required')
    b_add.classList.add('add')
    b_add.appendChild(tag_with_text('center', '+'))
    process_node.appendChild(b_add)
    b_add.onclick = cb_get_add_stage(stages, 'required')

}


function store_stage(stage_node){
    var stage_obj = {}

    stage_obj.name = stage_node.getName()
    stage_obj.status = stage_node.getStatus()
    stage_obj.result = stage_node.getResult()

    var type = stage_node.getType()
    stage_obj.function = store_function(type, stage_node.getFunction())

    stage_obj.requiredStages = []
    var rs = stage_node.getRequiredStages()
    for (var i = 0; i < rs.length; i++) {
        stage_obj.requiredStages.push(store_stage(rs[i]))
    }

    stage_obj.optionalStages = []
    var os = stage_node.getOptionalStages()
    for (var i = 0; i < os.length; i++) {
        stage_obj.optionalStages.push(store_stage(os[i]))
    }

    return stage_obj
}


function store_process(process_node) {
    var obj = new Object()

    obj.name = process_node.getName()
    obj.stages = []

    for (var i = 0; i < process_node.stages.length; i++) {
        obj.stages.push(store_stage(process_node.stages[i]))
    }

    return obj
}


function cb_load_process() {

    var input = document.getElementById('fileinput')
    if (!input || !input.files || !input.files[0]) {
        console.log('invalid input')
        console.log(input)
        return
    }

    var process_node = document.getElementById('process')

    var fr = new FileReader();
    fr.onload = function(e) {
        load_process(process_node, JSON.parse(e.target.result))
    }
    fr.readAsText(input.files[0])
}

function cb_save_process() {

    var stored = store_process(document.getElementById('process'))
    var stringified = JSON.stringify(stored, null, 2)

    download(stringified, stored.name + '.json', 'text')
}


function cb_upload() {

    var request = new XMLHttpRequest(),
        process = document.getElementById('process'),
        path = '/api/process/'


    request.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            load_process(
                process, 
                JSON.parse(this.responseText)
            )
        }
        if (this.status == 403) {
            console.log(this.responseText)
        }
    }

    request.open("POST", path, true)
    request.setRequestHeader('Content-type', 'application/json; charset=utf-8');
    request.send(JSON.stringify(store_process(process)))

}


function cb_download() {

    var request = new XMLHttpRequest(),
        process = document.getElementById('process'),
        input = document.getElementById('download-input'),
        path = '/api/process/get?name=' + encodeURIComponent(input.value)

    request.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            load_process(
                process, 
                JSON.parse(this.responseText)
            )
        }
        if (this.status == 403) {
            console.log(this.responseText)
        }
    }


    request.open("GET", path, true)
    request.send()
}


function cb_run() {
    var request = new XMLHttpRequest(),
        process = document.getElementById('process'),
        path = '/api/process/run?name=' + encodeURIComponent(process.getName())

    request.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            load_process(
                process, 
                JSON.parse(this.responseText)
            )
        }
        if (this.status == 403) {
            console.log(this.responseText)
        }
    }

    request.open("GET", path, true)
    request.send()

}


window.onload = function() {
    var input = document.getElementById('fileinput')
    input.onchange = function() {
        if (input.files && input.files[0]) {
            var text = input.parentElement.firstChild
            text.innerHTML = input.files[0].name + ' '
        }
    }

    var process = document.getElementById('process')
    load_process(process, {name: 'unnamed', stages: []})
}
