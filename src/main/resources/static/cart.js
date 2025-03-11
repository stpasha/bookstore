document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".changeQuant").forEach(form => {
        form.addEventListener("click", async function (event) {
            if (event.target.id === "plus" || event.target.id === "minus") {
                event.preventDefault();

                let quantitySpan = form.querySelector("#quantity-planned");
                let quantityInput = form.querySelector("#product-quantity");
                let availableQuantity = parseInt(form.querySelector("#quantity-available").textContent, 10);
                let currentQuantity = parseInt(quantitySpan.textContent, 10);
                let productId = form.querySelector("#productId").value;
                let change = event.target.id === "plus" ? 1 : -1;

                if ((event.target.id === "plus" && currentQuantity < availableQuantity) ||
                    (event.target.id === "minus" && currentQuantity > 0)) {

                    try {
                        let response = await fetch(`/items/${productId}/product/${change}`, {
                            method: "PUT",
                            headers: {
                                "Content-Type": "application/json"
                            }
                        });

                        if (response.ok) {
                            let newQuantity = await response.json();
                            quantitySpan.textContent = newQuantity;
                            quantityInput.value = newQuantity;
                        } else {
                            console.warn("Ошибка при обновлении количества товара");
                        }
                    } catch (error) {
                        console.error("Ошибка сети:", error);
                    }
                }
            }
        });
    });
});