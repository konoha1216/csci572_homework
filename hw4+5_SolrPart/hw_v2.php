<?php
include 'SpellCorrector.php';
ini_set('memory_limit', -1);

// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');

$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$results = false;
$method = '';
$method = $_REQUEST['algo'];

$additionalParameters = array('sort'=>$method,'fl'=>array('title','og_url','id','og_description'));

if ($query)
{
  // The Apache Solr Client library should be on the include path
  // which is usually most easily accomplished by placing in the
  // same directory as this script ( . or current directory is a default
  // php include path entry in the php.ini)
  require_once('/home/konoha/Desktop/solr-php-client-master/Apache/Solr/Service.php');

  // create a new solr service instance - host, port, and webapp
  // path (all defaults in this example)
  $solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample');

  // if magic quotes is enabled then stripslashes will be needed
  if (get_magic_quotes_gpc() == 1)
  {
    $query = stripslashes($query);
  }

  // in production code you'll always want to use a try /catch for any
  // possible exceptions emitted  by searching (i.e. connection
  // problems or a query parsing error)
  try
  {

    $wordList = explode(' ', $query);
    $originalQuery = '';
    $correctQuery = '';    
    foreach($wordList as $word){
      $originalQuery .= $word . ' ';
      $correctQuery .= SpellCorrector::correct($word) . ' ';
      //echo "we are doing the correction!!!";
    }
    $originalQuery = trim($originalQuery);
    $correctQuery = trim($correctQuery);
    //echo $originalQuery;
    //echo '<br>';
    //echo $correctQuery. '<br>';

    $showError = false;
    //echo 'flag value:'. $_GET['flag']. '<br>';
    if($_GET['flag'] == 0){
      if(strtolower($correctQuery) != strtolower($originalQuery)){
	$showError=true;
	$spellCorrectMessage1 = "show results for <b>" . $originalQuery . '</b>';
	$spellCorrectMessage2 = 'Search instead for <a href="javascript:void(0)" onclick="submit()">' . $correctQuery . '</a>';
      }
      $searchQuery = $originalQuery;
    }else{
      $searchQuery=$correctQuery;
    }
    //echo 'searchQuery:'. $searchQuery. '<br>';
    $results = $solr->search($searchQuery, 0, $limit,$additionalParameters);
  }
  catch (Exception $e)
  {
    // in production you'd probably log or email this error to an admin
    // and then show a special message to the user but for this example
    // we're going to show the full exception
    die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  }
}

?>
<html>
  <head>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
    <title>PHP Solr Client Example</title>
  </head>
  <body>
    <form  accept-charset="utf-8" method="get">
      <label for="q">Search:</label>
      <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
      <input type="text" id="flag" name="flag" value="0" hidden>
      <input type="radio" name="algo" value="score desc" <?php echo $method=="score desc"? 'checked':''; ?>>default
      <input type="radio" name="algo" value="pageRankFile desc" <?php echo $method=="pageRankFile desc"? 'checked':''; ?>>pagerank
      <br>
      <input type="submit" id="submit"/>
    </form>
    <div>
      <?php
        if($showError){
	  echo '<h4>' . $spellCorrectMessage1 . '</h4>';
	  echo '<h5>' . $spellCorrectMessage2 . '</h5><br>';
	}
      ?>
    </div>

<?php

// display results
if ($results)
{
  $total = (int) $results->response->numFound;
  $start = min(1, $total);
  $end = min($limit, $total);
?>
    <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
    <ol>
<?php
  // iterate result documents
  foreach ($results->response->docs as $doc)
  {
?>
      <li>
        <table style="border-bottom: 1px solid black; text-align: left; margin-bottom:20px;">
<?php

    foreach ($doc as $field => $value)
    {
	$id = isset($doc->id) ? $doc->id:'N/A';
	$title = isset($doc->title) ? $doc->title:'N/A';
	$url = isset($doc->og_url) ? $doc->og_url:'N/A';
	$description = isset($doc->og_description) ? $doc->og_description:'N/A';						
    }
?>
	<tr>
	  <th>title</th>
	  <td><a href="<?php echo $url; ?>" style="text-decoration:none;"><?php echo $title; ?></a></td>
	</tr>
	<tr>
	  <th>url</th>
	  <td><a href="<?php echo $url; ?>"><?php echo $url; ?></a></td>
	</tr>
	<tr>
	  <th>description</th>
	  <td><?php echo $description; ?></td>
	</tr>
	<tr>
	  <th>id</th>
	  <td><?php echo $id; ?></td>
	</tr>
        </table>
      </li>
<?php
  }
?>
    </ol>
<?php
}
?>
  <script type="text/javascript">
    $(function() {
        $("#q").autocomplete({
            source: function(request, response) {
            var URL = "http://localhost:8983/solr/myexample/suggest?q=" + $("#q").val();
            $.getJSON(URL, function(data) {
                var tempList = [];
                var wordList = data["suggest"]["suggest"][$("#q").val()]["suggestions"];
                for (var i = 0; i < wordList.length; i++) {
                    tempList.push(wordList[i]["term"]);
                }
                response(tempList);
            });
        }
        });
    });

    function submit(){
      document.getElementById("flag").value = "1";
      document.getElementById("submit").click();
    }
  </script>
  </body>
</html>
