
function popup(message) {
	$(document.body).append('<div id="dialog"><<div id="dialog-overlay">/div><div id="dialog-box"><div class="dialog-content"><div id="dialog-message"></div><a onclick="delete_dialog()" class="button">Inchide</a></div></div></div>')
	// get the screen height and width  
	var maskHeight = $(document).height();  
	var maskWidth = $(window).width();
	
	// calculate the values for center alignment
	var dialogTop =  (maskHeight/3) - ($('#dialog-box').height());  
	var dialogLeft = (maskWidth/2) - ($('#dialog-box').width()/2); 
	
	// assign values to the overlay and dialog box
	$('#dialog-overlay').css({height:maskHeight, width:maskWidth});
	$('#dialog-box').css({top:dialogTop, left:dialogLeft});
	$('#dialog').fadeIn();
	
	// display the message
	$('#dialog-message').html(message);
			
}
function trimite(lat, lon) {
	var nume = 0; var tel = 0; var email = 0;var grup=0;var descriere = 0;
	$(document.body).append('<div id="dialog"><div id="dialog-overlay"></div><div id="dialog-box"><div class="dialog-content"><div id="dialog-message"></div><a id = "inchide" class="button">Trimite</a><a onclick = "delete_dialog()" class="button">Inchide</a></div></div></div>')
	// get the screen height and width  
	var maskHeight = $(document).height();  
	var maskWidth = $(window).width();
	
	// calculate the values for center alignment
	var dialogTop =  (maskHeight/3) - ($('#dialog-box').height());  
	var dialogLeft = (maskWidth/2) - ($('#dialog-box').width()/2); 
	
	// assign values to the overlay and dialog box
	$('#dialog-overlay').css({height:maskHeight, width:maskWidth});
	$('#dialog-box').css({top:dialogTop, left:dialogLeft});
	$('#dialog').fadeIn();
	
	// display the message
	$('#dialog-message').append('<form name = "date" method = "post" action = "" ><span>Nume : &nbsp;&nbsp;&nbsp;</span><input type="text" name = "nume" /><br /><span>Grup : </span><input type="text" name = "grup" /><br /><<span>Email :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input type="text" name = "email" /><br />span>Telefon : </span><input type="text" name = "telefon" /><span>Descriere :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input type="text" name = "descriere" /><br />br /></form>');
	$('#inchide').click(function ()
  {
	nume = $("input[name=nume]").val();
	email = $("input[name=email]").val();
        grup = $("input[name=grup]").val();			
	telefon = $("input[name=telefon]").val();
	descriere = $("input[name=descriere]").val();
	$('#dialog-message').html('Loading...');
	$('.button').remove();
        $.get("cumpara-tot.php",{nume : nume,email : email,grup : grup,telefon : telefon, descriere : descriere})

														.done(function() {
																	   
																	 
																delete_dialog();
																popup('Punctul a fost adugat');
																location.reload();
																		})
														.fail(function() { return 0; })
							}

	});
}
function delete_dialog()
{
	$('#dialog').remove();

		//$('#dialog').remove();
	

}

function confirm_dialog(message, callback) {
	$(document.body).append('<div id="dialog"><div id="dialog-overlay"></div><div id="dialog-box"><div class="dialog-content"><div id="dialog-message"></div><a id="da" class="button">Da</a><a  id="nu" class="button">Nu</a></div></div></div>')
	// get the screen height and width  
	var maskHeight = $(document).height();  
	var maskWidth = $(window).width();
	
	// calculate the values for center alignment
	var dialogTop =  (maskHeight/3) - ($('#dialog-box').height());  
	var dialogLeft = (maskWidth/2) - ($('#dialog-box').width()/2); 
	
	// assign values to the overlay and dialog box
	$('#dialog-overlay').css({height:maskHeight, width:maskWidth});
	$('#dialog-box').css({top:dialogTop, left:dialogLeft});
	$('#dialog').fadeIn();
	
	// display the message
	$('#dialog-message').html(message);
	$('#da').click(function() { delete_dialog(); callback(true); });
	$('#nu').click(function() { delete_dialog(); callback(false); });
			
}


