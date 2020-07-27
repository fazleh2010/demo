'use strict';
const refreshInterval = 5000;
const template_replacements = {
	"service_name": "Terme-à-LLOD"
};

var task_status = {
};

const STATUS_PREFIX = {
	"done": '<span class="fa-stack"><i class="fas fa-list-ul"></i><i class="fas fa-check-square fa-badge"></i></span>',
	"success": '<span class="fa-stack"><i class="fas fa-list-ul"></i><i class="fas fa-badge fa-exclamation-circle"></i></span>',
	"active": '<span class="fa-stack"><i class="fas fa-tasks"></i><i class="fas fa-spinner fa-badge fa-pulse"></i></span>',
	"planned": '<span class="fa-stack"><i class="fas fa-tasks"></i><i class="far fa-square fa-badge fa-invert"></i></span>',
	"failed": '<span class="fa-stack"><i class="fas fa-tasks"></i><i class="fas fa-badge fa-exclamation-circle"></i></span>'
};

// https://stackoverflow.com/questions/8211744/convert-time-interval-given-in-seconds-into-more-human-readable-form
function forHumans ( seconds ) {
    var levels = [
        [Math.floor(seconds / 31536000), 'y'],
        [Math.floor((seconds % 31536000) / 86400), 'd'],
        [Math.floor(((seconds % 31536000) % 86400) / 3600), 'h'],
        [Math.floor((((seconds % 31536000) % 86400) % 3600) / 60), 'm'],
        [(((seconds % 31536000) % 86400) % 3600) % 60, 's'],
    ];
    var returntext = '';

    for (var i = 0, max = levels.length; i < max; i++) {
        if ( levels[i][0] === 0 ) continue;
        returntext += ' ' + levels[i][0] + ' ' + (levels[i][0] === 1 ? levels[i][1].substr(0, levels[i][1].length-1): levels[i][1]);
    };
    return returntext.trim();
}

function addPipelineElement(pl_current, pl_idx, pl_len, pipelineElement, data) {
	if (!pipelineElement) {
		return;
	}
	if (pl_current == undefined) { pl_current = -1; }
	var $target = $("ul#task_status");

	var $li = $("<li>");
	var data_status = data[pipelineElement];
	var data_error = data_status && data_status.error;

	// console.log("add pipeline element", pl_idx, pl_len, pipelineElement, data_status, data_error);

	var pipelinePrefix = "planned";
	if (pl_current > -1 && pl_idx <= pl_current) {
		pipelinePrefix = "done";
		if (data_status && data_status.status === 'success') {
			pipelinePrefix = 'success';
		} else if (!data_status || (data_status && data_status.status && data_status.status === 'failed') || data_error) {
			pipelinePrefix = 'failed';
		}
	}
	if (data_status && data_status.status === 'success') {
			pipelinePrefix = 'success';
    }

	if (STATUS_PREFIX.hasOwnProperty(pipelinePrefix)) {
		var $pipelinePrefix = $(STATUS_PREFIX[pipelinePrefix]);
		$li.append($pipelinePrefix);
	}
	if (pipelinePrefix) {
		$li.addClass("pipeline-element-" + pipelinePrefix);
	}

	var $pipelineTitle = $("<span></span>");
	$pipelineTitle.text(pipelineElement);
	$li.append($pipelineTitle);
	
	if (data_status && (data_status.meta || (data_status.time_start && data_status.time_end))) {
		var $pipeline_meta = $("<span></span>");
		$pipeline_meta.addClass("task-meta");

		if (data_status.meta) {
			var $meta_text = $("<span></span>");
			$meta_text.text(data_status.meta);
			$pipeline_meta.append($meta_text);
		}
		if (data_status.time_start && data_status.time_end) {
			var $meta_time = $("<span></span>");
			var duration = (data_status.time_end - data_status.time_start) / 1000.0;
			if (duration > 0) {
			$pipeline_meta.append($('<i class="fas fa-stopwatch"></i>'));
			$meta_time.text(" " + forHumans(duration));
			$pipeline_meta.append($meta_time);
			}
		}
		$li.append($pipeline_meta);
	}
	$target.append($li);

	if (data_status && data_status.message) {
		var $taskmessage = $("<pre></pre>");
		$taskmessage.addClass("task_message");
		$taskmessage.text(data_status.message);
		var $msgli = $("<li></li>");
		$msgli.addClass("task_more");
		$msgli.append($taskmessage);
		$target.append($msgli);

	}
	if (data_status && data_status.error && data_status.error.message) {
		var $errormessage = $("<pre></pre>");
		$errormessage.addClass("error_message");
		if (data_status.error.message && data_status.error.stack) {
			$errormessage.text(data_status.error.message + "\n\n" + data_status.error.stack);
		} else {
			$errormessage.text(data_status.error.message);
		}
		var $msgli = $("<li></li>");
		$msgli.addClass("task_more");
		$msgli.append($errormessage);
		$target.append($msgli);
	}

	if (data_status && data_status.links && data_status.links.length) {
		var $linkli = $("<li></li>");
		$linkli.addClass("task_more");
		$linkli.addClass("task_links");
		// console.log(data_status.links);
		data_status.links.forEach(function(link) {
			var $linkElem = $("<a />");
			$linkElem.addClass("resource-link");
			$linkElem.attr("href", link.path);
			$linkElem.attr("title", link.title);
			if (link.type && link.type == 'download') {
				$linkElem.append('<i class="fas fa-file-download"></i>');
			} else {
				$linkElem.append('<i class="fas fa-link"></i>');
			}
			var $linkTitle = $("<span></span>");
			$linkTitle.text(" " + link.title);
			$linkElem.append($linkTitle);
			// console.log($linkli, $linkElem);
			$linkli.append($linkElem);
		});
		$target.append($linkli);
	}

}

function refresh_status(task_status) {
	// console && console.log("refresh_status");
	var $statuselement = $("span.container-status-text");
	var $logtarget = $("pre#log-messages");
	// $statuselement.text('refreshing...');
	var $heartbeat = $("i#heartbeat");
	$heartbeat.addClass("swing");
	$heartbeat.removeClass("check-failed");

	var statusXHR = $.getJSON("./status.json", function(data) {
		if (!data || !data.status) {
			$statuselement.text("unknown");
		} else {
			$statuselement.text(data.status);
		}

		let log_messages = "no log entries yet";
		if (data && data.log && data.log.length) {
			log_messages = data.log.join("\n");
		}
		$logtarget.text(log_messages);

		if (data.pipeline) {
			var $target = $("ul#task_status");
			$target.empty();
			let $pipeline_progress_container = $("#pipeline-progress-container");
			let $pipeline_progress_current = $("#pipeline-progress-current");
			let $pipeline_progress_total = $("#pipeline-progress-total");

			if (data.pipeline.active) {
				$pipeline_progress_container.addClass("pipeline-active");
				$pipeline_progress_container.removeClass("pipeline-inactive");
			} else {
				$pipeline_progress_container.addClass("pipeline-inactive");
				$pipeline_progress_container.removeClass("pipeline-active");
			}
			if ((data.pipeline.current != undefined 
				&& data.pipeline.active) || (data.pipeline.current != undefined && data.pipeline.current == (data.pipeline.elements.length - 1))) {
				$pipeline_progress_current.text(data.pipeline.current + 1);
			} else {
				$pipeline_progress_current.text("-");
			}
			if (data.pipeline.length != undefined) {
				$pipeline_progress_total.text(data.pipeline.length);
			}

			if (data.pipeline.length && data.pipeline.elements) {
				for (let plI = 0, plL = data.pipeline.elements.length; plI < plL; plI++) {
				let pipelineElement = data.pipeline.elements[plI];
				addPipelineElement(data.pipeline.current || -1, plI, plL, pipelineElement, data);
				}
			}
		}
	}).fail(function(err) {
		console && console.log("error", err);

		$statuselement.text('check failed: ' + err.statusText + " (" + err.status + ")");
		$heartbeat.addClass("check-failed");
	}).always(function() {
		setTimeout(function() {
		$heartbeat.removeClass("swing");
		}, 2000);
		console.log("enqueueing next status update, interval=" + refreshInterval);
		setTimeout(refresh_status, refreshInterval);
	});
	
}

function update_template() {
	for (let k in template_replacements) {
		var v = template_replacements[k];
		if (!k || !v) continue;
		$(".tpl_" + k).text(v);
	}
}

function sparql_query() {
    $("#sparql_submit").click(function() {
        let query = $("textarea#query").val();
        if (!query) {
            return false;
        }
        var $heartbeat = $("i#heartbeat");
        $heartbeat.addClass("swing");
        $heartbeat.removeClass("check-failed");
	    var queryXHR = $.post("./sparql.json", {query: query}, function(data) {
            console.log("ok", data);
            let tgt = $("div#sparql_results");
            tgt.empty();
            if (data.error) {
                var errtitle = $("<strong></strong>");
                errtitle.text("Error");
                errtitle.addClass("sparql_error");
                tgt.append(errtitle);
                
                var errmsg = $("<span></span>");
                errmsg.text(data.error);
                errmsg.addClass("sparql_error");
                tgt.append(errmsg);
            }
            if (data.result) {
                let tgtul = $("<ul></ul>");
                tgtul.addClass("sparql_result_list");

                if (data.result.length) {
                    let keySet = [];
                    for (var lidx = 0; lidx < data.result.length; lidx++) {
                        let row = data.result[lidx];
                        let rowkeys = Object.keys(row);
                        rowkeys.forEach(function(k) {
                            if (keySet.indexOf(k) === -1) {
                                keySet.push(k);
                            }
                        });
                    }

                    let tgtli = $("<li></li>");
                    tgtli.addClass("rowheader");
                    keySet.forEach(function(key) {
                        let tgtfield = $("<span></span>");
                        tgtfield.text(key);
                        tgtli.append(tgtfield);
                    });
                    tgtul.append(tgtli);
                    
                    for (var lidx = 0; lidx < data.result.length; lidx++) {
                        let row = data.result[lidx];
                        let tgtli = $("<li></li>");

                        if (Object.keys(row).length) {
                            keySet.forEach(function(key) {
                                let tgtfield = $("<span></span>");
                                let val = null;
                                if (key in row) {
                                    let rowval = row[key];
                                    if (rowval && rowval.type && rowval.value && rowval.type == "uri") {
                                        let link = $("<a></a>")
                                        link.attr("href", rowval.value)
                                        link.text(rowval.value)
                                        tgtfield.append(link);
                                    } else if (rowval && rowval.type && rowval.value && rowval.type == "typed-literal") {
                                        val = rowval.value;
                                        // {"type":"typed-literal","datatype":"http://www.w3.org/2001/XMLSchema#integer","value":"109660"}
                                        tgtfield.text(val);
                                    } else {
                                        val = JSON.stringify(row[key]);
                                        tgtfield.text(val);
                                    }
                                }

                                tgtli.append(tgtfield);
                            });
                        } else {
                            tgtli.text(JSON.stringify(row));
                        }
                        tgtul.append(tgtli);
                    }
                }
                tgt.append(tgtul);
            } else {
                var errtitle = $("<strong></strong>");
                errtitle.text("no results");
                errtitle.addClass("sparql_error");
                tgt.append(errtitle);
            }

        }, "json").fail(function(err) {
            console && console.log("error", err);

            $statuselement.text('check failed: ' + err.statusText + " (" + err.status + ")");
            $heartbeat.addClass("check-failed");
        }).always(function() {
            setTimeout(function() {
            $heartbeat.removeClass("swing");
            }, 2000);
            console.log("enqueueing next status update, interval=" + refreshInterval);
            setTimeout(refresh_status, refreshInterval);
        });
        return false;
    });
}

$(document).ready(function() {
	update_template();
	refresh_status();
    sparql_query();
});

