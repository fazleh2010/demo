<?php

require './easyrdf-0.9.0/vendor/autoload.php';
$infile = "/tmp/server/uploads/current.rdf";
$inputformat = "turtle";
$outputformat = "ntriples";
$outfile = "/tmp/server/uploads/current.nt.gz";
$uri = "http://tbx2rdf";

$graph = new EasyRdf_Graph($uri);
echo "Loading file".PHP_EOL;
$content = file_get_contents($infile);
echo "Parsing input file".PHP_EOL;
$graph->parse($content, $inputformat, $uri);
// $me = $foaf->primaryTopic();
//echo "My name is: ".$me->get('foaf:name')."\n";
echo "Parsing complete. Serializing to $outputformat".PHP_EOL;
$content = $graph->serialise($outputformat);
echo "Serialization complete. Gzipping.".PHP_EOL;
$content = gzencode($content, 8);
echo "Writing output file $outfile.".PHP_EOL;
file_put_contents($outfile, $content);
echo "All done.".PHP_EOL;
