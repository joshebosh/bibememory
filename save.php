<?php

$c = $_POST["c"]; //TEXT FROM THE FIELD

function console_log($output, $with_script_tags = true) {
    $js_code = 'console.log(' . json_encode($output, JSON_HEX_TAG) . ')';
    if ($with_script_tags) {
        $js_code = '<script>' . $js_code . '</script>';
    }
    echo $js_code;
}
//console_log($c);

$f = 'kjv.txt'; //FILE TO SAVE (FILENAME EDITABLE)
$o = fopen($f, 'w+'); //OPENS IT
$w = fwrite($o, $c); //SAVES FILES HERE
$r = fread($o, 100000); //READS HERE
fclose($o); //CLOSES AFTER IT SAVES

//DISPLAYS THE RESULTS
if($w){
    echo 'File saved';
} else {
    echo 'Error saving file';
}

?>
