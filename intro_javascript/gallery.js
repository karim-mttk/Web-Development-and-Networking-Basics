/*Name this external file gallery.js*/

function upDate(previewPic) {
  document.getElementById("image").style.backgroundImage =
    "url('" + previewPic.src + "')";
  document.getElementById("image").innerHTML = previewPic.alt;
}

function unDo() {
  document.querySelector("#image").style.backgroundImage = "url('')";
}

//function to copy the same shipping name and zip to the billing details.
function billingFunction() {
  if (document.querySelector("#same").checked) {
    var name = document.querySelector("#shippingName").value;
    var zip = document.querySelector("#shippingZip").value;

    document.querySelector("#billingName").value = name;
    document.querySelector("#billingZip").value = zip;
  } else {
    document.querySelector("#billingName").value = "";
    document.querySelector("#billingZip").value = "";
  }
}
