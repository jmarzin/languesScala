/**
 * Created by jacques on 03/03/15.
 */
function deleteDialog(e) {
    var r = confirm("Confirmer la suppression");
    if (r == true) {
        return true;
    } else {
        e.preventDefault();
    }
}

function constructSortWord() {
    var value = $(".in_french").val().replace("'"," ").split(" ");
    var motsVides = ["l","la","le","les","aux","d","de","du","des","un","une"];
    while (value.length != 0) {
        if (motsVides.indexOf(value[0]) >= 0) {
            value.shift();
        } else {
            break;
        };
    };
    if (value.length != 0) {
        $(".sort_word").val(value[0]);
    };
}
