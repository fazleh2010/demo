async function fetch_json(uri) {
    const response = await fetch(uri);
    return await response.json();
}

(async function() {
    const domTarget = document.getElementById("terminology_browser");
    if (!domTarget) {
        console.error("terminology browser: DOM target not found");
    }
    console.log("terminology browser: intializing");

    const metadataTarget = document.getElementById("terminology_browser_meta");
    function clearElements(target) {
        if (!target) { return; }
        while (target.firstChild) {
            target.removeChild(target.firstChild);
        }
    }

    const language_data = await fetch_json("./static/lang/language-codes_json.json");
    const alpha2_label = {}
    language_data.forEach((o) => {
        if (o.English.indexOf(";") > -1) {
            o.English = o.English.split(";")[0].trim();
        }
        alpha2_label[o.alpha2] = o.English;
    });
    console.log("loaded labels for", Object.keys(alpha2_label).length, "languages");

    const terms_meta = await fetch_json("./terms");
    console.log("terms_meta", terms_meta);
    clearElements(metadataTarget);
    if (terms_meta.languages && Object.keys(terms_meta.languages).length > 0) {
        let languageList = document.createElement("ul");
        languageList.setAttribute("class", "termbrowser-meta-langlist");
        for (const [alpha2, count] of Object.entries(terms_meta.languages)) {
            const cur_label = alpha2_label[alpha2] || alpha2;
            const item = document.createElement("li");

            item.textContent = `${cur_label}: ${count}`;

            languageList.appendChild(item);
        }
        metadataTarget.appendChild(languageList);
    }

    const searchField = document.getElementById("q");
    searchField.addEventListener("keydown", function(e) {
        const query_string = e.target.value;
        if (query_string.length < 3) { return; }
            
    });

	const ac = new autoComplete({
		data: {
		  src: async () => {
			const query = document.querySelector("input#q").value;
			if (query.length < 3) { return; }
			const source = await fetch(`./terms?complete=${query}`);
			const data = await source.json();
			console.log("autocomplete/query", query, "result:", data);
			if (!data.autocomplete) { return []; }

			const acresults = [];
			const max_per_language = 6;
			
			for (const [alpha2, langresults] of Object.entries(data.autocomplete)) {
				if (!langresults) { continue; }

				langresults.forEach((langitem, idx) => {
                    if ((idx+1) > max_per_language) { return; }
					const item = {"language": alpha2, "term": langitem};
					acresults.push(JSON.stringify(item));
				});
			}
			
			return acresults;
		  },
		  cache: false
		},
		placeHolder: "Search...",
		selector: "#q",
		threshold: 2,
		debounce: 300,
		searchEngine: "strict",
		resultsList: {
			render: true,
			container: source => {
				source.setAttribute("id", "ac_resultlist");
			},
			destination: document.querySelector("input#q"),
			position: "afterend",
			element: "ul"
		},
		maxResults: 30,
		highlight: false, 
		resultItem: {
			content: (data, source) => {
                data = JSON.parse(data.value);
                console.log(data);
                const entry_lang = alpha2_label[data.language] || data.language;
                const entry_iri = data.term.iri;
                const entry_rep = data.term.rep;
				source.innerHTML = "";
                const match_link = document.createElement("a");
                match_link.setAttribute("href", `#${entry_iri}`);
                match_link.addEventListener("click", () => false);

                const rep_span = document.createElement("span");
                rep_span.setAttribute("class", "ac_result_rep");
                rep_span.textContent = entry_rep;
                match_link.appendChild(rep_span);

                const lang_span = document.createElement("span");
                lang_span.setAttribute("class", "ac_result_lang");
                lang_span.textContent = entry_lang;
                match_link.appendChild(lang_span);

                source.appendChild(match_link);
			},
			element: "li"
		},
		noResults: () => {
			const result = document.createElement("li");
			result.setAttribute("class", "no_result");
			result.setAttribute("tabindex", "1");
			result.innerHTML = "No Results";
			document.querySelector("#ac_results").appendChild(result);
		},
		onSelection: function(feedback) {
			document.querySelector("#q").blur();
            const sel = JSON.parse(feedback.selection.value);
			document.querySelector("#q").value = "";
			document.querySelector("#q").setAttribute("placeholder", sel.term.rep);
            const o = {iri: sel.term.iri, lang: sel.language};
            window.location.hash=JSON.stringify(o);
		},
	});

    function addTermInfo(target, terminfo, colname, colvalue) {
        const lielem = document.createElement("li");
        const namespan = document.createElement("span");
        namespan.setAttribute("class", "tb_namespan");
        namespan.textContent = colname + ":";

        const valspan = document.createElement("span");
        if (colvalue) {
            valspan.setAttribute("class", "tb_valspan");
            valspan.textContent = colvalue;
        } else {
            valspan.setAttribute("class", "tb_valspan tb_novalue");
            valspan.textContent = "-";
        }

        lielem.appendChild(namespan);
        lielem.appendChild(valspan);

        target.appendChild(lielem);
    }

    async function updateBrowser(hash) {
        if (!hash) { return; }
        if (hash.length < 2) { return; }
        console.log(hash);
        hash = decodeURIComponent(hash.substring(1));
        try {
            console.log(hash);
            hash = JSON.parse(hash);
        } catch (err) {
            console.error("failed to decode hash:", err);
            return;
        }

        console.log("update", hash);

        const termlookup = await fetch_json(`./terms?lookup=${JSON.stringify(hash)}`);
        console.log("termlookup", termlookup);

        if (!termlookup) { return; }

        clearElements(domTarget);
        domTarget.setAttribute("class", "");

        let written_header = false;

        termlookup.lookup.term_details.forEach((terminfo) => {
            if (!written_header) {
                const tb_theader = document.createElement("h4");
                tb_theader.textContent = `Term "${terminfo.writtenRep.value}"`
                domTarget.appendChild(tb_theader);
                written_header = true;
            }

            if (terminfo.language && terminfo.language.value) {
                terminfo.language.value = terminfo.language.value.split("/")
                terminfo.language.value = terminfo.language.value[terminfo.language.value.length -1].toLowerCase();
            }
            if (terminfo.language.value && terminfo.language.value === "unk") {
                terminfo.language.value = terminfo.writtenRep['xml:lang'];
            }

            const terminfoList = document.createElement("ul");
            terminfoList.setAttribute("class", "tb_terminfolist");
            
            addTermInfo(terminfoList, terminfo, "Language", (terminfo.language.value in alpha2_label) ? alpha2_label[terminfo.language.value] : terminfo.language.value);
            addTermInfo(terminfoList, terminfo, "Written Representation", terminfo.writtenRep.value || "");
            addTermInfo(terminfoList, terminfo, "uri", terminfo.entry.value|| "");
            addTermInfo(terminfoList, terminfo, "Definition", "");
            addTermInfo(terminfoList, terminfo, "Reliability Code", "");
            addTermInfo(terminfoList, terminfo, "Administrative Status", "");
            addTermInfo(terminfoList, terminfo, "SubjectField", "");
            addTermInfo(terminfoList, terminfo, "Reference", "");
            addTermInfo(terminfoList, terminfo, "POS", "");
            addTermInfo(terminfoList, terminfo, "Number", "");
            addTermInfo(terminfoList, terminfo, "Gender", "");
            addTermInfo(terminfoList, terminfo, "Hypernym", "");
            addTermInfo(terminfoList, terminfo, "Hyponym", "");
            addTermInfo(terminfoList, terminfo, "Variant", "");
            addTermInfo(terminfoList, terminfo, "Synonym", "");

            domTarget.appendChild(terminfoList);

            console.log("terminfo", terminfo);
        });

        const tb_header = document.createElement("h4");
        tb_header.textContent = "Linked Terms"
        domTarget.appendChild(tb_header);

        if (!termlookup.lookup.linked_terms || termlookup.lookup.linked_terms.length === 0) {
            const tb_nolinked = document.createElement("div");
            tb_nolinked.setAttribute("class", "tb_nolinked");
            tb_nolinked.textContent = "No linked terms found";
            domTarget.appendChild(tb_nolinked);
        } else {
            const tb_linklist = document.createElement("ul");
            tb_linklist.setAttribute("class", "tb_linklist");
            termlookup.lookup.linked_terms.forEach((term) => {
                const tb_linkitem = document.createElement("a");
                tb_linkitem.setAttribute("href", term.exactmatch.value);
                tb_linkitem.setAttribute("onclick", "javascript:return false");
                tb_linkitem.textContent = term.exactmatch.value;
                tb_linklist.appendChild(tb_linkitem);
            });
            domTarget.appendChild(tb_linklist);
        }
    }

    window.addEventListener("hashchange", () => updateBrowser(window.location.hash), false);
    updateBrowser(window.location.hash);
})();



