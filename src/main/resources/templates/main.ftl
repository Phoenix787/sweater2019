<#import "parts/common.ftl" as c>


<@c.page>
<div class="form-row">
    <div class="form-group col-md-6">
        <form method="get" action="/main" class="form-inline">
            <input type="text" name="filter" class="form-control" value="${filter!}" placeholder="Найти">
            <button type="submit" class="btn btn-primary ml-2">Найти</button>
        </form>
    </div>
</div>
<a class="btn btn-primary" data-toggle="collapse" href="#collapseExample" role="button" aria-expanded="false"
   aria-controls="collapseExample">
    Новое сообщение
</a>
<div class="collapse <#if message??> show </#if>" id="collapseExample">
    <div class="form-group mt-3">
        <form method="post" enctype="multipart/form-data">
            <div class="form-group">
                <input class="form-control ${(textError??)?string('is-invalid', '')}" type="text"
                       value="<#if message??>${message.text}</#if>" name="text" placeholder="Введите сообщение"/>
                <#if textError??>
                    <div class="invalid-feedback">
                        ${textError}
                    </div>
                </#if>
            </div>
            <div class="form-group">
                <input class="form-control ${(tagError??)?string('is-invalid', '')}" type="text"
                       value= "<#if message??>${message.tag}</#if>" name="tag" placeholder="Тэг">
                <#if tagError??>
                    <div class="invalid-feedback">
                        ${tagError}
                    </div>
                </#if>
            </div>
            <div class="form-group">
                <div class="custom-file">
                    <input class="form-control" type="file" name="file" id="customFile">
                    <label class="custom-file-label" for="customFile">Choose file</label>
                </div>
            </div>
            <input type="hidden" name="_csrf" value="${_csrf.token}"/>
            <div class="form-group">
                <button type="submit" class="btn btn-primary ml-2">Добавить</button>
            </div>
        </form>
    </div>
</div>

    <div class="card-columns">
    <#list messages as message>

        <div class="card my-3">
            <div>
            <#if message.filename??>
                <img class="card-img-top" src="/img/${message.filename}">
            </#if>
            </div>
            <div class="m-2">
                <span>${message.text}</span>
                <i>${message.tag}</i>
            </div>
            <div class="card-footer text-muted">
                ${message.authorName}
            </div>
        </div>

    <#else >
        No messages
    </#list>
    </div>
</@c.page>